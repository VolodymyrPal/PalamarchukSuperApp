package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import androidx.paging.PagingData
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface OrdersRepository {
    suspend fun ordersInRange(from: Date, to: Date): Flow<AppResult<List<Order>, AppError>>
    suspend fun getOrderById(id: Int): Flow<AppResult<Order?, AppError>>
    suspend fun softRefreshStatistic()

    fun pagingOrders(status: OrderStatus?): Flow<PagingData<Order>>
}