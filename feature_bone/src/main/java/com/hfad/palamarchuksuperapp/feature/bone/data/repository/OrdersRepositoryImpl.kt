package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.mapApiException
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.mediator.OrderRemoteMediator
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
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
            pagingSourceFactory = { orderDao.getOrdersWithServices(status) }
        ).flow.map { pagingData ->
            pagingData.map { orderEntityWithServices ->
                orderEntityWithServices.toDomain()
            }
        }
    }

    override suspend fun ordersInRange(
        from: Date,
        to: Date,
    ): AppResult<List<Order>, AppError> {

        return fetchWithCacheFallback<List<Order>>(
            fetchRemote = { orderApi.getOrdersWithRange(from, to).map { it.toDomain() } },
            saveRemoteAndFetchLocal = { orders ->
                boneDatabase.withTransaction {
                    orderDao.insertOrIgnoreOrders(orders.map { it.toEntity() })
                    val a: List<OrderEntityWithServices> = orderDao.ordersInRange(from, to)
                    a.map { it.toDomain() }
                }
            },
            fetchLocal = {
                orderDao.ordersInRange(from, to).map { it.toDomain() }
            }
        )
    }

    override suspend fun getOrderById(id: Int): AppResult<Order?, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { orderApi.getOrder(id)?.toDomain() },
            saveRemoteAndFetchLocal = {
                if (it != null) orderDao.insertOrIgnoreOrders(listOf(it.toEntity()))
                orderDao.getOrderById(id)?.toDomain()
            },
            fetchLocal = {
                orderDao.getOrderById(id)?.toDomain()
            }
        )
    }

    override suspend fun softRefreshStatistic(): AppResult<OrderStatistics, AppError> {
        return fetchWithCacheFallback<OrderStatistics>(
            fetchRemote = {
                orderApi.getOrderStatistics()
            },
            saveRemoteAndFetchLocal = {
                orderDao.insertOrIgnoreOrderStatistic(it)
                orderDao.getOrderStatistics()
            },
            fetchLocal = {
                orderDao.getOrderStatistics()
            }
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

suspend fun <T : Any?> fetchWithCacheFallback(
    fetchRemote: suspend () -> T,
    saveRemoteAndFetchLocal: suspend (T) -> T,
    fetchLocal: suspend () -> T,
): AppResult<T, AppError> {

    val remoteData = try {
        fetchRemote()
    } catch (apiEx: Exception) {
        return try {
            val localData = fetchLocal()
            AppResult.Error(data = localData, error = mapApiException(apiEx))
        } catch (dbEx: SQLException) {
            AppResult.Error(AppError.CustomError("Problem with internet and database: ${apiEx.message}, ${dbEx.message}"))
        }
    }

    return try {
        val localData = saveRemoteAndFetchLocal(remoteData)
        AppResult.Success(localData)
    } catch (dbEx: SQLException) {
        AppResult.Error(data = remoteData, error = mapSQLException(dbEx))
    }
}