package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FinanceRepository {
    fun operationsFromTo(from: Date, to: Date): AppResult<Flow<List<TypedTransaction>>, AppError>
    val statistic: AppResult<Flow<FinanceStatistics>, AppError>
}