package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.di.FeatureScope
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.userSession
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@FeatureScope
class AuthRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient, // For api call of token
    private val context: Context, //For encrypted shared preferences or other context-related operations
) : AuthRepository {
    private val mutex = Mutex()
    private val sessionConfig = SessionConfig()

    override suspend fun login(
        username: String,
        password: String,
        isRemembered: Boolean,
    ): AppResult<Boolean, AppError> = mutex.withLock {
        val now = Date()
        val expiresAt = Date(now.time + sessionConfig.sessionDuration.time)

        val session = UserSession(
            username = username,
            accessToken = "access_token",   // TODO: Replace with real API call for token refresh
            refreshToken = "refresh_token", // TODO: Replace with real API call for token refresh
            loginTimestamp = now,
            expiresAt = expiresAt,
            rememberSession = isRemembered,
            userStatus = LogStatus.LOGGED_IN
        )
        saveSession(session)

        AppResult.Success(true)
    }

    override suspend fun refreshToken(): AppResult<Unit, AppError> = mutex.withLock {
        val currentSession = observeCurrentSession().first() ?: return@withLock AppResult.Error(
            AppError.SessionError.SessionNotFound("No active session found")
        )
        val now = Date()
        val updatedSession = currentSession.copy(
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token",
            expiresAt = Date(now.time + sessionConfig.sessionDuration.time),
            userStatus = LogStatus.LOGGED_IN
        )
        val saveResult = saveSession(updatedSession)
        if (saveResult is AppResult.Error) {
            return@withLock saveResult
        }
        AppResult.Success(Unit)
    }

    override suspend fun logout() {
        mutex.withLock {
            context.userSession.edit { preferences ->
                preferences.clear() // Clear user session data
            }
        }
    }

    override val currentSession: Flow<UserSession> =
        context.userSession.data.map { preferences ->
            buildSessionFromPrefs(preferences)
                ?: return@map UserSession()
        }.distinctUntilChanged()

    override suspend fun saveSession(session: UserSession): AppResult<Unit, AppError> {
        val maxRetries = 3
        var lastException: IOException? = null

        repeat(maxRetries) { attempt ->
            try {
                context.userSession.edit { prefs ->
                    prefs[USERNAME_KEY] = session.username
                    prefs[ACCESS_TOKEN_KEY] = session.accessToken
                    prefs[REFRESH_TOKEN_KEY] = session.refreshToken
                    prefs[LOGIN_TIMESTAMP_KEY] = session.loginTimestamp.time
                    prefs[EXPIRES_AT_KEY] = session.expiresAt.time
                    prefs[IS_REMEMBERED_KEY] = session.rememberSession
                    prefs[IS_LOGGED_KEY] = true
                    prefs[USER_STATUS_KEY] = session.userStatus.name
                }
                return AppResult.Success(Unit)
            } catch (e: IOException) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    println("Failed to save session, retry attempt ${attempt + 1}/$maxRetries: ${e.message}") //TODO logging
                    delay(1000)
                }
            }
        }

        return AppResult.Error(
            AppError.SessionError.SessionNotFound(
                lastException?.message ?: "Failed to save session after $maxRetries attempts"
            )
        )
    }


    override fun observeCurrentSession(): Flow<UserSession?> {
        return context.userSession.data.map {
            buildSessionFromPrefs(it)
        }
    }

    override fun shouldRefreshToken(session: UserSession): Boolean {
        val refreshDate = Date(session.expiresAt.time - sessionConfig.refreshThreshold.time)
        return Date().after(refreshDate)
    }

    private fun buildSessionFromPrefs(prefs: Preferences): UserSession? {
        val isLogged = prefs[IS_LOGGED_KEY] ?: false
        if (!isLogged) return null

        val userStatusString = prefs[USER_STATUS_KEY] ?: LogStatus.NOT_LOGGED.name
        val userStatus = LogStatus.valueOf(userStatusString)

        return UserSession(
            username = prefs[USERNAME_KEY] ?: return null,
            accessToken = prefs[ACCESS_TOKEN_KEY] ?: return null,
            refreshToken = prefs[REFRESH_TOKEN_KEY] ?: return null,
            loginTimestamp = Date(prefs[LOGIN_TIMESTAMP_KEY] ?: return null),
            expiresAt = Date(prefs[EXPIRES_AT_KEY] ?: return null),
            rememberSession = prefs[IS_REMEMBERED_KEY] ?: false,
            userStatus = userStatus
        )
    }


    data class UserSession(
        val username: String = "",
        val accessToken: String = "",
        val refreshToken: String = "",
        val loginTimestamp: Date = Date(),
        val expiresAt: Date = Date(),
        val rememberSession: Boolean = false,
        val userStatus: LogStatus = LogStatus.NOT_LOGGED,
    )

    companion object {
        private val IS_LOGGED_KEY = booleanPreferencesKey("is_logged")
        private val LOGIN_TIMESTAMP_KEY = longPreferencesKey("login_timestamp")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val IS_REMEMBERED_KEY = booleanPreferencesKey("is_remembered")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_PERMISSION_KEY = stringPreferencesKey("user_permission")
        private val USER_STATUS_KEY = stringPreferencesKey("user_status")
    }
}

data class SessionConfig(
    val sessionDuration: Date = Date(4.days.inWholeMilliseconds),
    val refreshThreshold: Date = Date(11.days.inWholeMilliseconds),
    val maxRetryAttempts: Int = 3,
    val autoRefreshEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = false,
)

enum class AppPermission {
    ORDERS,
    PAYMENTS,
    SALES,
    FINANCE
}

enum class LogStatus {
    LOGGED_IN,
    REQUIRE_WEAK_LOGIN,
    TOKEN_REFRESH_REQUIRED,
    TOKEN_AUTO_REFRESH,
    NOT_LOGGED
}