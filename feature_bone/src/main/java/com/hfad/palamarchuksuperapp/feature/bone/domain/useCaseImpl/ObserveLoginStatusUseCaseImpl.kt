package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.UserSession
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
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
) : ObserveLoginStatusUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<LogStatus> = authRepository.currentSession
        .mapLatest { session -> determineLoginStatus(session) }
        .distinctUntilChanged()

    private fun determineLoginStatus(session: UserSession): LogStatus {

        if (session.userStatus == LogStatus.NOT_LOGGED) {
            return LogStatus.NOT_LOGGED
        }

        val now = Date().time
        val loginTime = session.loginTimestamp.time

        if (!session.rememberSession &&
            now - loginTime >= sessionConfig.sessionTimeout
        ) {
            return LogStatus.NOT_LOGGED
        }

        val refreshThresholdEnd = loginTime + sessionConfig.refreshThreshold
        val activeSessionEnd = loginTime + sessionConfig.sessionTokenDuration

        if (sessionConfig.tokenRefreshEnable &&
            now in (refreshThresholdEnd + 1)..activeSessionEnd
        ) {
            return LogStatus.TOKEN_REFRESH_REQUIRED
        }

        return when {
            now <= refreshThresholdEnd -> LogStatus.LOGIN_ALLOWED
            now <= activeSessionEnd -> LogStatus.REQUIRE_WEAK_LOGIN
            else -> LogStatus.NOT_LOGGED
        }
    }
}