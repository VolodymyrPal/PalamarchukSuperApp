package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

interface RefreshTokenUseCase {
    suspend operator fun invoke(): AppResult<Unit, AppError>
}
