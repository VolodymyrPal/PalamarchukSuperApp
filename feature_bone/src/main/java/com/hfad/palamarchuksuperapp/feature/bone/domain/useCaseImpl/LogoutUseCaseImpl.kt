package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ClearAllDatabaseUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import jakarta.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val clearAllDatabaseUseCase: ClearAllDatabaseUseCase,
) : LogoutUseCase {
    override suspend fun invoke() {
        clearAllDatabaseUseCase()
        authRepository.logout()
    }
}