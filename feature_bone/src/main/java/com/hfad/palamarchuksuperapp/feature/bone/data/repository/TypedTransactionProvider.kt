package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction

interface TypedTransactionProvider {
    suspend fun getTypedTransactionInRange(
        from: Long,
        to: Long,
    ): AppResult<List<TypedTransaction>, AppError>
}