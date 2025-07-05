package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String,
        isRemembered: Boolean = false,
    ): AppResult<Boolean, AppError>

    suspend fun refreshToken(): AppResult<Unit, AppError>

    suspend fun logout()

    fun observeCurrentSession(): Flow<UserSession?>

    fun shouldRefreshToken(session: UserSession): Boolean

    suspend fun saveUpdateSession(session: UserSession): AppResult<Unit, AppError>

    val currentSession: Flow<UserSession>
}