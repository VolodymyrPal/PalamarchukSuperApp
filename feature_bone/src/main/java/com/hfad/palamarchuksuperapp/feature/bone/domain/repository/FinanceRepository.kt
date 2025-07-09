package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    val operations: AppResult<Flow<List<TypedTransaction>>, AppError>
    val statistic: AppResult<Flow<FinanceStatistic>, AppError>
    suspend fun getOperationById(id: Int): AppResult<TypedTransaction, AppError>
    suspend fun softRefreshOperations()
    suspend fun hardRefreshOperations()
}