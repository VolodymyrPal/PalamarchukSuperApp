package com.hfad.palamarchuksuperapp.feature.bone.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val database: BoneDatabase,
    private val orderApi: OrderApi,
    private val statusList: List<OrderStatus>,
) : RemoteMediator<Int, OrderEntityWithServices>() {

    val orderDao = database.orderDao()
    val remoteKeysDao = database.remoteKeysDao()

    private val statusFilter: String = statusList.joinToString(",") { it.name }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH //TODO need better refresh logic to remove frequent requests
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OrderEntityWithServices>,
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) return MediatorResult.Success(endOfPaginationReached = false)
                val keys = database.remoteKeysDao().remoteKeysOrderId(lastItem.order.id, statusFilter)
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val response = orderApi.getOrdersByPage(page, state.config.pageSize, statusList)
            val endReached = response.size < state.config.pageSize

            database.withTransaction {
                val keys = response.map {
                    OrderRemoteKeys(
                        id = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1,
                        statusFilter = statusFilter
                    )
                }
                if (loadType == LoadType.REFRESH) {
//                    orderDao.deleteOrdersByStatus(status) //TODO for testing
//                    remoteKeysDao.clearRemoteKeysByStatus(status)
                }
                orderDao.insertOrIgnoreOrders(response.map { it.toEntity() }) //TODO
                remoteKeysDao.insertAll(keys)
            }

            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}