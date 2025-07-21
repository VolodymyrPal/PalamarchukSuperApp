package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface OrdersRepository {
    val cachedOrders: Flow<AppResult<List<Order>, AppError>>
    val cachedOrderStatistics: Flow<AppResult<OrderStatistics, AppError>>
    suspend fun ordersByPage(page: Int, size: Int): Flow<AppResult<List<Order>, AppError>>
    suspend fun ordersInRange(from: Date, to: Date): Flow<AppResult<List<Order>, AppError>>
    suspend fun getOrderById(id: Int): Flow<AppResult<Order?, AppError>>
    suspend fun softRefreshOrders()
    suspend fun hardRefreshOrders()
}