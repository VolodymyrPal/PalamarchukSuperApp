package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.feature.bone.di.FeatureScope
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.userSession
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@FeatureScope
class AuthRepository @Inject constructor(
    private val httpClient: HttpClient, // For api call of token
    private val context: Context, //For encrypted shared preferences or other context-related operations
) {
    private val mutex = Mutex()
    val sessionConfig = SessionConfig()

    suspend fun login(
        username: String,
        password: String,
        isRemembered: Boolean = false,
    ): Result<Boolean, AppError> = mutex.withLock {
        val now = Date()
        val expiresAt = Date(now.time + sessionConfig.sessionDuration)

        val session = UserSession(
            username = username,
            accessToken = "access_token",
            refreshToken = "refresh_token",
            loginTimestamp = now,
            expiresAt = expiresAt,
            rememberSession = isRemembered
        )
        saveSession(session)

        Result.Success(true)
    }

    suspend fun refreshToken(): Result<UserSession, AppError> = mutex.withLock {
        val currentSession = getCurrentSession()
            ?: return Result.Error(
                AppError.NetworkException.ApiError.CustomApiError(
                    message = "No current session found"
                )
            )

        try {
            val now = Date()
            val updatedSession = currentSession.copy(
                accessToken = "new_access_token",
                refreshToken = "new_refresh_token",
                expiresAt = Date(now.time + sessionConfig.sessionDuration)
            )

            saveSession(updatedSession)
            Result.Success(updatedSession)
        } catch (e: Exception) {
            logout()
            Result.Error(
                AppError.NetworkException.ApiError.CustomApiError(
                    message = "Failed to refresh token: ${e.message}",
                    cause = e
                )
            )
        }
    }

    suspend fun logout() {
        context.userSession.edit { preferences ->
            preferences.clear() // Clear user session data
        }
    }

    fun isSessionValid(session: UserSession): Boolean {
        val now = Date()

        return when {
            now.before(session.expiresAt) -> true
//            shouldRefreshToken(session = session) && sessionConfig.autoRefreshEnabled -> {
//                refreshToken() is Result.Success
//            }
            else -> false
        }
    }

    val logSuccess: Flow<Boolean> = context.userSession.data
        .onStart {
            context.userSession.data.first().let { prefs ->
                if (prefs[IS_REMEMBERED_KEY] != true) {
                    logout()
                }
            }
        }
        .map { prefs ->
            val session = buildSessionFromPrefs(prefs) ?: return@map false
            isSessionValid(session)
        }

    val sessionFlow: Flow<UserSession> = context.userSession.data
        .map { preferences ->
            buildSessionFromPrefs(preferences) ?: UserSession()
        }
        .distinctUntilChanged()

    private suspend fun saveSession(session: UserSession) {
        context.userSession.edit { prefs ->
            prefs[USERNAME_KEY] = session.username
            prefs[ACCESS_TOKEN_KEY] = session.accessToken
            prefs[REFRESH_TOKEN_KEY] = session.refreshToken
            prefs[LOGIN_TIMESTAMP_KEY] = session.loginTimestamp.time
            prefs[EXPIRES_AT_KEY] = session.expiresAt.time
            prefs[IS_REMEMBERED_KEY] = session.rememberSession
            prefs[IS_LOGGED_KEY] = true
        }
    }

    private suspend fun getCurrentSession(): UserSession? {
        return context.userSession.data.first().let { prefs ->
            buildSessionFromPrefs(prefs)
        }
    }

    private fun shouldRefreshToken(session: UserSession): Boolean {
        val refreshDate = Date(session.expiresAt.time - sessionConfig.refreshThreshold.time)
        return Date().after(refreshDate)
    }

    private fun buildSessionFromPrefs(prefs: Preferences): UserSession? {
        val isLogged = prefs[IS_LOGGED_KEY] ?: false
        if (!isLogged) return null

        return UserSession(
            username = prefs[USERNAME_KEY] ?: return null,
            accessToken = prefs[ACCESS_TOKEN_KEY] ?: return null,
            refreshToken = prefs[REFRESH_TOKEN_KEY] ?: return null,
            loginTimestamp = Date(prefs[LOGIN_TIMESTAMP_KEY] ?: return null),
            expiresAt = Date(prefs[EXPIRES_AT_KEY] ?: return null),
            rememberSession = prefs[IS_REMEMBERED_KEY] ?: false
        )
    }


    data class SessionConfig(
        val sessionDuration: Date = Date(4.days.inWholeMilliseconds),
        val refreshThreshold: Date = Date(11.days.inWholeMilliseconds),
        val maxRetryAttempts: Int = 3,
        val autoRefreshEnabled: Boolean = false,
        val biometricAuthEnabled: Boolean = false,
    )

    data class UserSession(
        val username: String = "",
        val accessToken: String = "",
        val refreshToken: String = "",
        val loginTimestamp: Date = Date(),
        val expiresAt: Date = Date(),
        val rememberSession: Boolean = false,
    )

    companion object {
        private val IS_LOGGED_KEY = booleanPreferencesKey("is_logged")
        private val LOGIN_TIMESTAMP_KEY = longPreferencesKey("login_timestamp")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val IS_REMEMBERED_KEY = booleanPreferencesKey("is_remembered")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}

enum class LogStatus {
    LOGGED_IN,
    REQUIRE_WEAK_LOGIN,
    TOKEN_REFRESH_REQUIRED,
    TOKEN_AUTO_REFRESH,
    NOT_LOGGED
}