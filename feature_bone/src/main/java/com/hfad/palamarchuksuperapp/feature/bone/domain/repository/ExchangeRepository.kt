package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ExchangeRepository {
    val exchanges: AppResult<Flow<List<ExchangeOrder>>, AppError>
    suspend fun getExchangeById(id: Int): AppResult<ExchangeOrder, AppError>
    suspend fun softRefreshExchanges()
    suspend fun hardRefreshExchanges()
    suspend fun syncedDateForPeriod(from: Date, to: Date) : Flow<List<ExchangeOrder>>
}