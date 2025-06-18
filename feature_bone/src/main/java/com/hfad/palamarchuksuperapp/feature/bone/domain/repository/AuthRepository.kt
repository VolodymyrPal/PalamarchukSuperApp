package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String,
        isRemembered: Boolean = false,
    ): Result<Boolean, AppError>

    suspend fun refreshToken(): Result<AuthRepositoryImpl.UserSession, AppError>

    suspend fun logout()

    suspend fun getCurrentSession(): AuthRepositoryImpl.UserSession?

    fun observeCurrentSession(): Flow<AuthRepositoryImpl.UserSession?>

    val logStatus: Flow<LogStatus>
}