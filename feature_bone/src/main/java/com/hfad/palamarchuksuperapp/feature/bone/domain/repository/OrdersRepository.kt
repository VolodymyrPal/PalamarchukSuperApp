package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    val orders: AppResult<Flow<List<Order>>, AppError>
    val orderStatistic: AppResult<Flow<OrderStatistic>, AppError>
    suspend fun getOrderById(id: Int): AppResult<Order, AppError>
    suspend fun softRefreshOrders()
    suspend fun hardRefreshOrders()
}