package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    val exchanges: AppResult<Flow<List<ExchangeOrder>>, AppError>
    suspend fun getExchangeById(id: Int): AppResult<ExchangeOrder, AppError>
    suspend fun softRefreshExchanges()
    suspend fun hardRefreshExchanges()
}