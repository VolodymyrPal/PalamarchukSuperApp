package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

interface LoginWithCredentialsUseCase {
    suspend operator fun invoke(
        username: String,
        password: String,
        isRemembered: Boolean = false,
    ): AppResult<Boolean, AppError>
}
