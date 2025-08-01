package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.data.fetchWithCacheFallback
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
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val boneDatabase: BoneDatabase,
    private val orderApi: OrderApi,
) : OrdersRepository {

    private val orderDao = boneDatabase.orderDao()

    @OptIn(ExperimentalPagingApi::class)
    private val pagerCache = mutableMapOf<OrderStatus?, Flow<PagingData<Order>>>()

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingOrders(status: OrderStatus?): Flow<PagingData<Order>> {
        return pagerCache.getOrPut(status) {
            Pager(
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

    override suspend fun softRefreshStatistic(): AppResult<OrderStatistics, AppError> {
        return fetchWithCacheFallback(
            fetchRemote = {
                orderApi.getOrderStatistics()
            },
            storeAndRead = {
                boneDatabase.withTransaction {
                    orderDao.insertOrIgnoreOrderStatistic(it)
                    orderDao.getOrderStatistics()
                }
            },
            fallbackFetch = {
                orderDao.getOrderStatistics()
            }
        )
    }
}