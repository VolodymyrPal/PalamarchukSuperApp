package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.net.SocketException
import java.nio.channels.UnresolvedAddressException
import java.util.Date

class OrdersRepositoryImpl //@Inject constructor
    (
    private val orderDatabase: OrderDao,
    private val orderApi: OrderApi,
) : OrdersRepository {

    override val cachedOrders: Flow<AppResult<List<Order>, AppError>> =
        orderDatabase.cachedOrders.withSqlErrorHandling()

    override val cachedOrderStatistics: Flow<AppResult<OrderStatistics, AppError>> =
        orderDatabase.cachedOrderStatistics.withSqlErrorHandling()

    override suspend fun ordersByPage(page: Int, size: Int): Flow<AppResult<List<Order>, AppError>> = flow {
        emit(AppResult.Success(emptyList()))

        val offset = (page - 1) * 10

        val daoResult = withSqlErrorHandling { orderDatabase.getOrdersByPage(page) }
        emit(daoResult)

        val apiOrders = orderApi.getOrdersByPage(page)
        if (apiOrders is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrders(apiOrders.data)
            emit(AppResult.Success(apiOrders.data))
        } else {
            emit(AppResult.Error((apiOrders as AppResult.Error).error, data = (daoResult as AppResult.Success).data))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun ordersInRange(
        from: Date,
        to: Date,
    ): Flow<AppResult<List<Order>, AppError>> {
        val apiOrders = orderApi.getOrdersWithRange(from, to)
        if (apiOrders is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrders(apiOrders.data)
        }
        return orderDatabase.ordersInRange(from, to).withSqlErrorHandling()
    }

    override suspend fun getOrderById(id: Int): Flow<AppResult<Order?, AppError>> {
        val orderApi = orderApi.getOrder(id)
        if (orderApi is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrders(listOf(orderApi.data))
        }
        return orderDatabase.getOrderById(id).withSqlErrorHandling()
    }

    override suspend fun softRefreshOrders() {
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrders(ordersResultApi.data)
        }

        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    override suspend fun hardRefreshOrders() {
        val ordersResultApi = getOrdersResultApiWithError()
        if (ordersResultApi is AppResult.Success) {
            orderDatabase.deleteAllOrders()
            orderDatabase.insertOrIgnoreOrders(ordersResultApi.data)
        }

        val orderStatisticResultApi = getOrderStatisticResultApiWithError()
        if (orderStatisticResultApi is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrderStatistic(orderStatisticResultApi.data)
        }
    }

    private suspend fun getOrdersResultApiWithError(): AppResult<List<Order>, AppError> {
        val ordersResultApi = orderApi.getOrdersByPage(1)
        if (ordersResultApi is AppResult.Success) {
            orderDatabase.insertOrIgnoreOrders(ordersResultApi.data)
        }
        return ordersResultApi
    }

    private suspend fun getOrderStatisticResultApiWithError(): AppResult<OrderStatistics, AppError> {
        return safeApiCall {
            val orderStatistics = orderApi.syncOrderStatistic()
            if (orderStatistics is AppResult.Success) {
                orderDatabase.insertOrIgnoreOrderStatistic(orderStatistics.data)
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

//@OptIn(ExperimentalPagingApi::class)
//class OrderRemoteMediator(
//    private val database: OrderDao,
//    private val orderApi: OrderApi
//) : RemoteMediator<Int, Order>() {
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, Order>
//    ): MediatorResult {
//        val page = when (loadType) {
//            LoadType.REFRESH -> 1
//            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//            LoadType.APPEND -> {
//                val lastItem = state.lastItemOrNull()
//                if (lastItem == null) return 1
//                val keys = database.remoteKeysDao().remoteKeysOrderId(lastItem.id)
//                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
//            }
//        }
//
//        return try {
//            val response = orderApi.getOrdersByPage(page, state.config.pageSize)
//            val endReached = response.isEmpty()
//
//            database.withTransaction {
//                if (loadType == LoadType.REFRESH) {
//                    database.orderDao().clearAll()
//                    database.remoteKeysDao().clearRemoteKeys()
//                }
//
//                val keys = response.map {
//                    OrderRemoteKeys(
//                        orderId = it.id,
//                        prevKey = if (page == 1) null else page - 1,
//                        nextKey = if (endReached) null else page + 1
//                    )
//                }
//
//                database.remoteKeysDao().insertAll(keys)
//                database.orderDao().insertAll(response.map { it.toEntity() })
//            }
//
//            MediatorResult.Success(endOfPaginationReached = endReached)
//        } catch (e: Exception) {
//            MediatorResult.Error(e)
//        }
//    }
//}

//class OrdersPagingSource (
//    private val orderApi: OrderApi
//) : PagingSource<Int, Order>() {
//    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
//        state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            return anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//        return null
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
//        val nextPageKey = params.key ?: 1
//        val response = orderApi.getOrdersByPage(nextPageKey) as AppResult.Success
//
//        return LoadResult.Page(
//            data = response.data,
//            prevKey = null,
//            nextKey = nextPageKey + 1
//        )
//    }
//
//}