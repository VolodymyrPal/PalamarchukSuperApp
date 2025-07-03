package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ObserveLoginStatusUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import java.util.Date

class ObserveLoginStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionConfig: SessionConfig,
    private val logoutUseCase: LogoutUseCase,
) : ObserveLoginStatusUseCase {
    private val sessionExtensionThresholdMs = 1_000L // Value to avoid save session duplication

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<LogStatus> = authRepository.currentSession
        .mapLatest { session -> determineLoginStatus(session) }
        .distinctUntilChanged()

    private suspend fun determineLoginStatus(session: AuthRepositoryImpl.UserSession): LogStatus {

        if (session.userStatus == LogStatus.NOT_LOGGED) {
            return LogStatus.NOT_LOGGED
        }
        val now = Date()

        if (!session.rememberSession &&
            (session.loginTimestamp.time + sessionConfig.sessionTimeout) <= now.time
        ) {
            logoutUseCase.invoke()
            return LogStatus.NOT_LOGGED
        }

        val activeSessionEnd =
            Date(session.loginTimestamp.time + sessionConfig.sessionTokenDuration)
        val refreshThresholdEnd =
            Date(session.loginTimestamp.time + sessionConfig.refreshThreshold)
        val shouldRefresh = authRepository.shouldRefreshToken(session)


        return when {
            now.before(activeSessionEnd) -> handleActiveSession(session, now)
            shouldRefresh && sessionConfig.autoRefreshEnabled -> LogStatus.TOKEN_AUTO_REFRESH
            shouldRefresh && !sessionConfig.autoRefreshEnabled -> LogStatus.TOKEN_REFRESH_REQUIRED
            now.before(refreshThresholdEnd) -> LogStatus.REQUIRE_WEAK_LOGIN
            else -> LogStatus.NOT_LOGGED
        }
    }

    private suspend fun handleActiveSession(
        session: AuthRepositoryImpl.UserSession,
        now: Date,
    ): LogStatus {

        val delta = now.time - session.loginTimestamp.time

        if (delta > sessionExtensionThresholdMs) {
            val extendedSession = session.copy(
                loginTimestamp = now,
            )
            return when (authRepository.saveSession(extendedSession)) {
                is AppResult.Error -> LogStatus.NOT_LOGGED
                else -> LogStatus.LOGGED_IN
            }
        }
        return LogStatus.LOGGED_IN
    }
}