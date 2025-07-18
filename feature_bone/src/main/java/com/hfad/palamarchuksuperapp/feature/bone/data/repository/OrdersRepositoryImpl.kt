package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneControllerDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val boneControllerDao: BoneControllerDao,
    private val boneApi: BoneApi,
) : OrdersRepository {

    override val cachedOrders: AppResult<Flow<List<Order>>, AppError> = trySqlApp {
        boneControllerDao.cachedOrders
    }

    override val cachedOrderStatistics: AppResult<Flow<OrderStatistics>, AppError> = trySqlApp {
        boneApi.syncOrderStatistic()
        boneControllerDao.cachedOrderStatistics
    }

    override fun ordersInRange(
        from: Date,
        to: Date,
    ): Flow<List<Order>> {
        boneApi.getOrdersWithRange(from, to)
        return boneControllerDao.ordersInRange(from, to)
    }

    override suspend fun getOrderById(id: Int): AppResult<Flow<Order?>, AppError> {
        val orderApi = boneApi.getOrder(id)
        val order = boneControllerDao.getOrderById(id) // ?: return AppResult.Error(AppError.NetworkError)
        return AppResult.Success(order)
    }

    override suspend fun softRefreshOrders() {
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrders(ordersResultApi.data)
        }

        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshOrders() {
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            boneControllerDao.deleteAllOrders()
            boneControllerDao.insertOrIgnoreOrders(ordersResultApi.data)
        }

        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    private suspend fun getOrdersResultApiWithError(): AppResult<List<Order>, AppError> {
        return safeApiCall {
            val orders: List<Order> = boneApi.getOrdersByPage(1)
            AppResult.Success(orders)
        }
    }

    private suspend fun getOrderStatisticResultApiWithError(): AppResult<OrderStatistics, AppError> {
        return safeApiCall {
            val orderStatistics: OrderStatistics = boneApi.syncOrderStatistic()
            AppResult.Success(orderStatistics)
        }
    }
}

private suspend fun some(): AppResult<Unit, AppError> {
    coroutineScope {
        appSafeApiCall {
            AppResult.Success(Unit)
        }
    }
    return trySqlApp {
        AppResult.Success<Unit, AppError>(Unit)
    }
}

fun <T> appSafeApiCall(call: () -> AppResult<T, AppError>): AppResult<T, AppError> {
    return try {
        call()
    } catch (e: UnresolvedAddressException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Problem with internet connection.",
                cause = e
            )
        )
    } catch (e: JsonConvertException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Problem with parsing response. Please contact developer.",
                cause = e
            )
        )
    } catch (e: ClosedReceiveChannelException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Waiting for response timeout. Please contact developer.",
                cause = e
            )
        )
    } catch (e: SocketException) { //Sometimes when bad connection occur
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Waiting for response timeout. Please contact developer.",
                cause = e
            )
        )
    }
}