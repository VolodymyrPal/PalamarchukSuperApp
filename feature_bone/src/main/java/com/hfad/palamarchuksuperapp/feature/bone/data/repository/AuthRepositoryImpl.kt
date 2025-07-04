package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.di.FeatureClient
import com.hfad.palamarchuksuperapp.feature.bone.di.FeatureScope
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.CryptoService
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.userSession
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@FeatureScope
class AuthRepositoryImpl @Inject constructor(
    @FeatureClient private val httpClient: HttpClient, // For api call of token
    private val context: Context, //For encrypted shared preferences or other context-related operations
    private val cryptoService: CryptoService,
) : AuthRepository {
    private val mutex = Mutex()
    private val sessionConfig = SessionConfig()

    override suspend fun login(
        username: String,
        password: String,
        isRemembered: Boolean,
    ): AppResult<Boolean, AppError> = mutex.withLock {
        val now = Date()

        val session = UserSession(
            username = username,
            accessToken = "access_token",   // TODO: Replace with real API call for token refresh
            refreshToken = "refresh_token", // TODO: Replace with real API call for token refresh
            loginTimestamp = now,
            rememberSession = isRemembered,
            userStatus = LogStatus.LOGGED_IN // TODO: IF LOGIN SUCCESSFUL
        )
        saveSession(session)

        AppResult.Success(true)
    }

    override suspend fun refreshToken(): AppResult<Unit, AppError> = mutex.withLock {
        val currentSession = observeCurrentSession().first() ?: return@withLock AppResult.Error(
            AppError.SessionError.SessionNotFound("No active session found")
        )
        val updatedSession = currentSession.copy(
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token",
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

    override val currentSession: Flow<UserSession> = context.userSession.data.map { preferences ->
        buildSessionFromPrefs(preferences) ?: return@map UserSession(
            username = "",
            accessToken = "",
            refreshToken = "",
            loginTimestamp = Date(),
            rememberSession = false,
            userStatus = LogStatus.NOT_LOGGED
        )
    }.distinctUntilChanged()

    override suspend fun saveSession(session: UserSession): AppResult<Unit, AppError> {
        val maxRetries = sessionConfig.maxRetryAttempts
        var lastException: IOException? = null
        val sessionJson = try {
            Json.encodeToString(session)
        } catch (e: Exception) {
            return AppResult.Error(AppError.SessionError.SessionCanNotBeJson("Failed to serialize session: ${e.message}"))
        }
        val encryptedSession = cryptoService.encrypt(sessionJson)


        repeat(maxRetries) { attempt ->
            try {
                context.userSession.edit { prefs ->
                    prefs[ENCRYPTED_SESSION_KEY] = encryptedSession
                }
                return AppResult.Success(Unit)
            } catch (e: IOException) {
                lastException = e
                if (attempt < maxRetries - 1) {
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
        val refreshWindowStartTime = session.loginTimestamp.time + sessionConfig.refreshThreshold
        return System.currentTimeMillis() >= refreshWindowStartTime
    }

    private fun buildSessionFromPrefs(prefs: Preferences): UserSession? {
        val encryptedSession = prefs[ENCRYPTED_SESSION_KEY] ?: return null
        val decrypted = cryptoService.decrypt(encryptedSession)
        if (decrypted == null || decrypted.isBlank()) return null
        val userSession = Json.decodeFromString<UserSession>(decrypted)
        return userSession
    }


    @Serializable
    data class UserSession(
        val username: String,
        val accessToken: String,
        val refreshToken: String,
        val rememberSession: Boolean,
        val userStatus: LogStatus,
        @Serializable(with = DateAsLongSerializer::class) val loginTimestamp:Date
    )

    companion object {
        private val ENCRYPTED_SESSION_KEY = stringPreferencesKey("encrypted_session")
    }
}

data class SessionConfig(
    val sessionTokenDuration: Long = 25.days.inWholeMilliseconds,
    val refreshThreshold: Long = 11.days.inWholeMilliseconds,
    val sessionTimeout: Long = 20.minutes.inWholeMilliseconds,
    val maxRetryAttempts: Int = 5,
    val tokenRefreshEnable: Boolean = false,
    val biometricAuthEnabled: Boolean = false,
)

enum class AppPermission {
    ORDERS, PAYMENTS, SALES, FINANCE
}

enum class LogStatus {
    LOGGED_IN,
    REQUIRE_WEAK_LOGIN,      // if session is past refresh threshold but within session duration
    TOKEN_REFRESH_REQUIRED,  // if auto refresh is disabled
    NOT_LOGGED
}

object DateAsLongSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DateAsLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}