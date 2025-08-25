package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    suspend fun operationsInRange(from: Long, to: Long): AppResult<List<TypedTransaction>, AppError>
    val financeStatistics: Flow<AppResult<FinanceStatistics, AppError>>
}