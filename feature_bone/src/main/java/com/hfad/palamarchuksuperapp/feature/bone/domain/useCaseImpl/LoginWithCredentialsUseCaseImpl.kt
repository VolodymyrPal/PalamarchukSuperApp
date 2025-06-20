package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LoginWithCredentialsUseCase
import jakarta.inject.Inject

class LoginWithCredentialsUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : LoginWithCredentialsUseCase {
    override suspend fun invoke(
        username: String,
        password: String,
        isRemembered: Boolean,
    ): AppResult<Boolean, AppError> {
        return authRepository.login(
            username = username,
            password = password,
            isRemembered = isRemembered
        )
    }
}