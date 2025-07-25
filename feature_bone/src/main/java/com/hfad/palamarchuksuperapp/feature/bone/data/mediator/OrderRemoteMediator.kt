package com.hfad.palamarchuksuperapp.feature.bone.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val database: BoneDatabase,
    private val orderApi: OrderApi,
    private val status: OrderStatus?,
) : RemoteMediator<Int, Order>() {

    val orderDao = database.orderDao()
    val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Order>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) return MediatorResult.Success(endOfPaginationReached = true)
                val keys = database.remoteKeysDao()
                    .remoteKeysOrderId(lastItem.id, status)
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        val response = orderApi.getOrdersByPage(page, state.config.pageSize)

        if (response is AppResult.Error) {
            return MediatorResult.Error(
                throwable = response.error.cause ?: Exception(response.error.message)
            )
        }

        response as AppResult.Success
        val endReached = response.data.size < state.config.pageSize

        database.withTransaction {
            val keys = response.data.map {
                OrderRemoteKeys(
                    id = it.id,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (endReached) null else page + 1,
                    filter = status
                )
            }
            if (loadType == LoadType.REFRESH) {
                orderDao.deleteAllOrders()
                remoteKeysDao.clearRemoteKeys()
            }
            orderDao.insertOrIgnoreOrders(response.data)
            remoteKeysDao.insertAll(keys)
        }
        return MediatorResult.Success(endOfPaginationReached = endReached)
    }
}