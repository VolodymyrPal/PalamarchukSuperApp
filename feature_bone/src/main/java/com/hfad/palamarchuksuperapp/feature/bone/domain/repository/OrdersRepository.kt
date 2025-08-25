package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import androidx.paging.PagingData
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.TypedTransactionProvider
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrdersRepository : TypedTransactionProvider {
    suspend fun getOrderById(id: Int): AppResult<Order?, AppError>
    suspend fun refreshStatistic() : AppResult<OrderStatistics, AppError>

    val orderStatistics: Flow<AppResult<OrderStatistics, AppError>>
    fun pagingOrders(status: List<OrderStatus>, query: String): Flow<PagingData<Order>>
}