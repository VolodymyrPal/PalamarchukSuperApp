package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneControllerDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.net.SocketException
import java.nio.channels.UnresolvedAddressException
import java.util.Date
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val boneControllerDao: BoneControllerDao,
    private val orderApi: OrderApi,
) : OrdersRepository {

    override val cachedOrders: Flow<AppResult<List<Order>, AppError>> =
        boneControllerDao.cachedOrders.withSqlErrorHandling()

    override val cachedOrderStatistics: Flow<AppResult<OrderStatistics, AppError>> =
        boneControllerDao.cachedOrderStatistics.withSqlErrorHandling()

    override suspend fun ordersInRange(
        from: Date,
        to: Date,
    ): Flow<AppResult<List<Order>, AppError>> {
        val apiOrders = orderApi.getOrdersWithRange(from, to)
        if (apiOrders is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrders(apiOrders.data)
        }
        return boneControllerDao.ordersInRange(from, to).withSqlErrorHandling()
    }

    override suspend fun getOrderById(id: Int): Flow<AppResult<Order?, AppError>> {
        val orderApi = orderApi.getOrder(id)
        if (orderApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrders(listOf(orderApi.data))
        }
        return boneControllerDao.getOrderById(id).withSqlErrorHandling()
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
        val ordersResultApi = orderApi.getOrdersByPage(1)
        if (ordersResultApi is AppResult.Success) {
            boneControllerDao.insertOrIgnoreOrders(ordersResultApi.data)
        }
        return ordersResultApi
    }

    private suspend fun getOrderStatisticResultApiWithError(): AppResult<OrderStatistics, AppError> {
        return safeApiCall {
            val orderStatistics = orderApi.syncOrderStatistic()
            if (orderStatistics is AppResult.Success) {
                boneControllerDao.insertOrIgnoreOrderStatistic(orderStatistics.data)
                return@safeApiCall orderStatistics
            }
            return@safeApiCall AppResult.Error(AppError.NetworkException.ApiError.UndefinedError())
        }
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

fun <T> Flow<T>.withSqlErrorHandling(): Flow<AppResult<T, AppError>> {
    return this
        .map { AppResult.Success<T, AppError>(it) as AppResult<T, AppError> }
        .catch { e ->
            if (e is SQLException) {
                emit(AppResult.Error<T, AppError>(mapSQLException(e)))
            } else {
                throw e
            }
        }
}