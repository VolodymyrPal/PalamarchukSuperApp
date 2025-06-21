package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.RefreshTokenUseCase
import jakarta.inject.Inject

class RefreshTokenUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
) : RefreshTokenUseCase {
    override suspend fun invoke(): AppResult<Unit, AppError> {
        val result = authRepository.refreshToken()

        return when (result) {
            is AppResult.Success -> {
                AppResult.Success(Unit)
            }

            is AppResult.Error -> {
                if (result.error is AppError.SessionError.SessionNotFound) {
                    logoutUseCase()
                }
                result
            }
        }
    }
}