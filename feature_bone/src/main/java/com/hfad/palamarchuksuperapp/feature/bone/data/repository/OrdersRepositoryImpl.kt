package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val boneDatabase: BoneDatabase,
    private val orderApi: OrderApi,
) : OrdersRepository {

    val orderDao = boneDatabase.orderDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingOrders(status: OrderStatus?): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = OrderRemoteMediator(
                orderApi = orderApi,
                database = boneDatabase,
                status = status
            ),
            pagingSourceFactory = { orderDao.getOrders(status) }
        ).flow
    }

    override suspend fun ordersInRange(
        from: Date,
        to: Date,
    ): Flow<AppResult<List<Order>, AppError>> {
        val apiOrders = orderApi.getOrdersWithRange(from, to)
        if (apiOrders is AppResult.Success) {
            orderDao.insertOrIgnoreOrders(apiOrders.data)
        }
        return orderDao.ordersInRange(from, to).withSqlErrorHandling()
    }

    override suspend fun getOrderById(id: Int): Flow<AppResult<Order?, AppError>> {
        val orderApi = orderApi.getOrder(id)
        if (orderApi is AppResult.Success) {
            orderDao.insertOrIgnoreOrders(listOf(orderApi.data))
        }
        return orderDao.getOrderById(id).withSqlErrorHandling()
    }

    override suspend fun softRefreshStatistic() {
        val ordersResultApi = orderApi.getOrderStatistics()
        if (ordersResultApi is AppResult.Success) {
            orderDao.insertOrIgnoreOrderStatistic(ordersResultApi.data)
        }
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