package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.fetchWithCacheFallback
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.data.tryApiRequest
import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.mediator.OrderRemoteMediator
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrderApi
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

    private val orderDao = boneDatabase.orderDao()


    @OptIn(ExperimentalPagingApi::class)
    private val pagerCache =
        mutableMapOf<Pair<List<OrderStatus>, String>, Flow<PagingData<Order>>>()

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingOrders(status: List<OrderStatus>, query: String): Flow<PagingData<Order>> {
        val key = status to query
        return pagerCache.getOrPut(key) {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = true),
                remoteMediator = OrderRemoteMediator(
                    orderApi = orderApi,
                    database = boneDatabase,
                    statusList = status,
                ),
                pagingSourceFactory = {
                    orderDao.getOrdersWithServices(status.takeIf { it.isNotEmpty() }, query)
                },
            )
                .flow
                .map { pagingData ->
                    pagingData.map { orderEntityWithServices ->
                        orderEntityWithServices.toDomain()
                    }
                }
        }
    }

    override suspend fun ordersInRange(
        from: Date,
        to: Date,
    ): AppResult<List<Order>, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { orderApi.getOrdersWithRange(from, to).map { it.toDomain() } },
            storeAndRead = { orders ->
                boneDatabase.withTransaction {
                    orderDao.insertOrIgnoreOrders(orders.map { it.toEntity() })
                    val localOrders: List<OrderEntityWithServices> =
                        orderDao.ordersInRange(from, to)
                    localOrders.map { it.toDomain() }
                }
            },
            fallbackFetch = {
                orderDao.ordersInRange(from, to).map { it.toDomain() }
            }
        )
    }

    override suspend fun getOrderById(id: Int): AppResult<Order?, AppError> {

        return fetchWithCacheFallback(
            fetchRemote = { orderApi.getOrder(id)?.toDomain() },
            storeAndRead = {
                boneDatabase.withTransaction {
                    if (it != null) orderDao.insertOrIgnoreOrders(listOf(it.toEntity()))
                    orderDao.getOrderById(id)?.toDomain()
                }
            },
            fallbackFetch = {
                orderDao.getOrderById(id)?.toDomain()
            }
        )
    }

    override suspend fun refreshStatistic(): AppResult<OrderStatistics, AppError> {
        val statisticApi = tryApiRequest { orderApi.getOrderStatistics() }
        return if (statisticApi is AppResult.Success) {
            val dbCall = withSqlErrorHandling {
                orderDao.insertOrUpdateStatistic(statisticApi.data.toEntity())
            }
            if (dbCall is AppResult.Error) {
                return AppResult.Error(dbCall.error, data = statisticApi.data.toDomain())
            }
            AppResult.Success(statisticApi.data.toDomain())
        } else {
            statisticApi as AppResult.Error
            AppResult.Error(statisticApi.error)
        }
    }

    override val orderStatistics: Flow<AppResult<OrderStatistics, AppError>> =
        orderDao.getStatistic().map {
            AppResult.Success<OrderStatistics, AppError>(it?.toDomain() ?: OrderStatistics())
        }.catch { e ->
            if (e is SQLException) {
                AppResult.Error<OrderStatistics, AppError>(mapSQLException(e))
            } else {
                AppResult.Error<OrderStatistics, AppError>(
                    AppError.CustomError(
                        "Unknown error",
                        cause = e
                    )
                )
            }
        }
}