package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import jakarta.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LogoutUseCase {
    override suspend fun invoke() {
        authRepository.logout()
    }
}