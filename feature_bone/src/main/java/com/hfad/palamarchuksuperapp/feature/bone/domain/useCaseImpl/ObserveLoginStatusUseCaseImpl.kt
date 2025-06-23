package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.di.AppFirstAccessDetector
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ObserveLoginStatusUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date

class ObserveLoginStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionConfig: SessionConfig,
    private val logoutUseCase: LogoutUseCase,
    private val detector: AppFirstAccessDetector,
) : ObserveLoginStatusUseCase {
    private val detectorKey = "isFirstAccess"

    override fun invoke(): Flow<LogStatus> = authRepository.currentSession
        .map { session -> determineLoginStatus(session) }
        .distinctUntilChanged()

    private suspend fun determineLoginStatus(session: AuthRepositoryImpl.UserSession): LogStatus {

        if (session.userStatus == LogStatus.NOT_LOGGED || session.expiresAt < Date()) {
            detector.isFirstAccess(detectorKey)
            return LogStatus.NOT_LOGGED
        }

        if (detector.isFirstAccess(detectorKey) && !session.rememberSession) {
            logoutUseCase()
            detector.isFirstAccess(detectorKey)
            return LogStatus.NOT_LOGGED
        }

        detector.isFirstAccess(detectorKey)

        val now = Date()
        val activeSessionEnd =
            Date(session.loginTimestamp.time + sessionConfig.sessionDuration.time)
        val refreshThresholdEnd =
            Date(session.loginTimestamp.time + sessionConfig.refreshThreshold.time)
        val shouldRefresh = authRepository.shouldRefreshToken(session)

        return when {
            now.before(activeSessionEnd) -> handleActiveSession(session, now)
            now.before(refreshThresholdEnd) -> LogStatus.REQUIRE_WEAK_LOGIN
            shouldRefresh && !sessionConfig.autoRefreshEnabled -> LogStatus.TOKEN_REFRESH_REQUIRED
            shouldRefresh && sessionConfig.autoRefreshEnabled -> LogStatus.TOKEN_AUTO_REFRESH
            else -> LogStatus.NOT_LOGGED
        }
    }

    private suspend fun handleActiveSession(
        session: AuthRepositoryImpl.UserSession,
        now: Date,
    ): LogStatus {
        if (detector.isFirstAccess("Extend_Session_On_Start")) {
            val extendedSession = session.copy(
                loginTimestamp = now,
                expiresAt = Date(now.time + sessionConfig.sessionDuration.time)
            )

            if (extendedSession != session) {
                return when (authRepository.saveSession(extendedSession)) {
                    is AppResult.Error -> LogStatus.NOT_LOGGED
                    else -> LogStatus.LOGGED_IN
                }
            }
            return LogStatus.LOGGED_IN
        }

        return LogStatus.LOGGED_IN
    }
}