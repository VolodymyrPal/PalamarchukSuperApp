package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction

interface GetTypeTransactionOperationsUseCase {
    suspend operator fun invoke(): AppResult<List<TypedTransaction>, AppError>
}
