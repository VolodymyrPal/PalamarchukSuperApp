package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val boneDao: BoneDao,
    private val boneApi: BoneApi,
) : OrdersRepository {

    override val orders: AppResult<Flow<List<Order>>, AppError> =
        trySqlApp {
            boneDao.orders
        }

    override val orderStatistic: AppResult<Flow<OrderStatistic>, AppError> =
        trySqlApp {
            boneDao.orderStatistic
        }

    override suspend fun getOrderById(id: Int): AppResult<Order, AppError> {
        return withSqlErrorHandling {
            boneDao.getOrderById(id)
        }
    }

    override suspend fun softRefreshOrders() {
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOrders(ordersResultApi.data)
        }
        
        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshOrders() {
        boneDao.deleteAllOrders()
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOrders(ordersResultApi.data)
        }
        
        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            boneDao.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    private suspend fun getOrdersResultApiWithError(): AppResult<List<Order>, AppError> {
        return safeApiCall {
            val orders: List<Order> = boneApi.getOrdersApi()
            AppResult.Success(orders)
        }
    }
    
    private suspend fun getOrderStatisticResultApiWithError(): AppResult<OrderStatistic, AppError> {
        return safeApiCall {
            val orderStatistic: OrderStatistic = boneApi.getOrderStatisticApi()
            AppResult.Success(orderStatistic)
        }
    }
}