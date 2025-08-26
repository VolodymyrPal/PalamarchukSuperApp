package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import androidx.paging.PagingData
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.TypedTransactionProvider
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import kotlinx.coroutines.flow.Flow

interface SalesRepository : TypedTransactionProvider {
    suspend fun getSaleOrderById(id: Int): AppResult<SaleOrder?, AppError>

    suspend fun refreshStatistic(): AppResult<SalesStatistics, AppError>

    val salesStatistics: Flow<AppResult<SalesStatistics, AppError>>

    fun pagingSales(status: List<SaleStatus>, query: String): Flow<PagingData<SaleOrder>>
}
