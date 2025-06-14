package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.userSession
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

class AuthRepository @Inject constructor(
    private val httpClient: HttpClient, // For api call of token
    private val context: Context, //For encrypted shared preferences or other context-related operations
) {
    suspend fun login(
        username: String,
        password: String,
    ) : Boolean {
        val request = httpClient.request {  }
        if (request.status.value != 200) {
            return false
        }
        return true
    }

    suspend fun logout() {
        context.userSession.edit { preferences ->
            preferences.clear() // Clear user session data
        }
    }

    suspend fun isLogged(): Boolean {
        return context.userSession.data.first().let { preferences ->
            val isLogged = preferences[IS_LOGGED_KEY] ?: false
            val loginTimestamp = preferences[LOGIN_TIMESTAMP_KEY] ?: 0L
            isLogged && loginTimestamp > 0
        }
    }

}


class LoggerDataStoreHandler(
    val context: Context,
) {
    private val sessionDuration = 30.days

    suspend fun setIsLogged(username: String = "") {
        context.userSession.edit { preferences ->
            preferences[IS_LOGGED_KEY] = true
            preferences[LOGIN_TIMESTAMP_KEY] = System.currentTimeMillis()
            if (username.isNotEmpty()) {
                preferences[USERNAME_KEY] = username
            }
        }
    }

    suspend fun clearUser() {
        context.userSession.edit { preferences ->
            preferences.remove(IS_LOGGED_KEY)
            preferences.remove(LOGIN_TIMESTAMP_KEY)
            preferences.remove(USERNAME_KEY)
        }
    }

    private fun isSessionValid(loginTimestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val sessionAge = (currentTime - loginTimestamp).milliseconds
        return sessionAge < sessionDuration
    }

    suspend fun validateSession() {
        val (isLogged, timestamp) = context.userSession.data.first().run {
            this[IS_LOGGED_KEY] to this[LOGIN_TIMESTAMP_KEY]
        }
        if (isLogged == true && timestamp != null && !isSessionValid(timestamp)) {
            clearUser()
        }
    }

    val isLoggedFlow: Flow<Boolean> = context.userSession.data
        .map { prefs ->
            val isLogged = prefs[IS_LOGGED_KEY] ?: false
            val timestamp = prefs[LOGIN_TIMESTAMP_KEY] ?: 0L
            isLogged && timestamp > 0 && isSessionValid(timestamp)
        }
        .distinctUntilChanged()


    val usernameFlow: Flow<String> = context.userSession.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
        .distinctUntilChanged()

    suspend fun getRemainingSessionTime(): Duration? {
        return context.userSession.data.first().let { preferences ->
            val isLogged = preferences[IS_LOGGED_KEY] ?: false
            val loginTimestamp = preferences[LOGIN_TIMESTAMP_KEY] ?: 0L

            if (isLogged && loginTimestamp > 0) {
                val currentTime = System.currentTimeMillis()
                val elapsed = (currentTime - loginTimestamp).milliseconds
                val remaining = sessionDuration - elapsed

                if (remaining.isPositive()) remaining else null
            } else {
                null
            }
        }
    }

    suspend fun extendSession() {
        context.userSession.edit { preferences ->
            if (preferences[IS_LOGGED_KEY] == true) {
                preferences[LOGIN_TIMESTAMP_KEY] = System.currentTimeMillis()
            }
        }
    }

    suspend fun initialize() {
        val prefs = context.userSession.data.first()
        if ((prefs[IS_LOGGED_KEY] == true) != (prefs[LOGIN_TIMESTAMP_KEY] != null)) {
            clearUser() // Автоматический сброс при несоответствии
        }
    }

    private suspend fun startValidationLoop() {
        while (true) {
            validateSession()
            delay(5 * 60 * 1000) // Проверка каждые 5 минут
        }
    }
}

private val IS_LOGGED_KEY = booleanPreferencesKey("is_logged")
private val LOGIN_TIMESTAMP_KEY = longPreferencesKey("login_timestamp")
private val USERNAME_KEY = stringPreferencesKey("username")