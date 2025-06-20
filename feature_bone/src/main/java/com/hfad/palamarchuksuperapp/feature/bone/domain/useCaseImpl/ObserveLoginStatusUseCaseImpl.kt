package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

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
import java.util.Date

class ObserveLoginStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : ObserveLoginStatusUseCase {
    private val sessionConfig: SessionConfig = SessionConfig()

    override fun invoke(): Flow<LogStatus> = authRepository.currentSession
        .map { session -> determineLoginStatus(session) }
        .distinctUntilChanged()

    private suspend fun determineLoginStatus(session: AuthRepositoryImpl.UserSession): LogStatus {
        if (session.userStatus == LogStatus.NOT_LOGGED) {
            return LogStatus.NOT_LOGGED
        }

        val now = Date()
        val activeSessionEnd =
            Date(session.loginTimestamp.time + sessionConfig.sessionDuration.time)
        val refreshThresholdEnd =
            Date(session.loginTimestamp.time + sessionConfig.refreshThreshold.time)
        val shouldRefresh = authRepository.shouldRefreshToken(session)

        return when {
            isWithinActiveSession(now, activeSessionEnd) -> {
                handleActiveSession(session, now)
            }

            isWithinRefreshWindow(now, refreshThresholdEnd) -> {
                LogStatus.REQUIRE_WEAK_LOGIN
            }

            shouldRefresh && !sessionConfig.autoRefreshEnabled -> {
                LogStatus.TOKEN_REFRESH_REQUIRED
            }

            shouldRefresh && sessionConfig.autoRefreshEnabled -> {
                LogStatus.TOKEN_AUTO_REFRESH
            }

            else -> LogStatus.NOT_LOGGED
        }
    }

    private fun isWithinActiveSession(now: Date, activeSessionEnd: Date): Boolean {
        return now.before(activeSessionEnd)
    }

    private fun isWithinRefreshWindow(now: Date, refreshThresholdEnd: Date): Boolean {
        return now.before(refreshThresholdEnd)
    }

    private suspend fun handleActiveSession(
        session: AuthRepositoryImpl.UserSession,
        now: Date,
    ): LogStatus {
        val extendedSession = session.copy(
            loginTimestamp = now,
            expiresAt = Date(now.time + sessionConfig.sessionDuration.time)
        )

        return when (authRepository.saveSession(extendedSession)) {
            is AppResult.Error -> LogStatus.NOT_LOGGED
            else -> LogStatus.LOGGED_IN
        }
    }
}