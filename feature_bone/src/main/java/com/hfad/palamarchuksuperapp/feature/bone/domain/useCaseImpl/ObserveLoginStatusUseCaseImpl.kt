package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.di.AppFirstAccessDetector
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ObserveLoginStatusUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Date

class ObserveLoginStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionConfig: SessionConfig,
    private val detector: AppFirstAccessDetector,
) : ObserveLoginStatusUseCase {
    private val detectorKey = "isFirstAccess"
    private val sessionExtensionThresholdMs = 1_000L // Value to avoid save session duplication

    override fun invoke(): Flow<LogStatus> = authRepository.currentSession
        .onStart {
            if (detector.isFirstAccess(detectorKey)) {
                authRepository.clearUnrememberedSession()
            }
        }
        .map { session -> determineLoginStatus(session) }
        .distinctUntilChanged()

//        flow {
//        if (detector.isFirstAccess(detectorKey)) {
//            authRepository.clearUnrememberedSession()
//        }
//
//        Log.d("2", "2")
//        emitAll(
//            authRepository.currentSession
//                .map { session -> determineLoginStatus(session) }
//                .distinctUntilChanged()
//        )
//    }


    private suspend fun determineLoginStatus(session: AuthRepositoryImpl.UserSession): LogStatus {
        val now = Date()

        if (session.userStatus == LogStatus.NOT_LOGGED) {
            return LogStatus.NOT_LOGGED
        }

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
            val newLoginTimestamp = now
            val newExpiresAt = Date(now.time + sessionConfig.sessionDuration.time)

            val delta = session.loginTimestamp.time - newLoginTimestamp.time

            if (delta > sessionExtensionThresholdMs) {
                val extendedSession = session.copy(
                    loginTimestamp = newLoginTimestamp,
                    expiresAt = newExpiresAt
                )

                return when (authRepository.saveSession(extendedSession)) {
                    is AppResult.Error -> LogStatus.NOT_LOGGED
                    else -> LogStatus.LOGGED_IN
                }
            }
        }

        return LogStatus.LOGGED_IN
    }
}