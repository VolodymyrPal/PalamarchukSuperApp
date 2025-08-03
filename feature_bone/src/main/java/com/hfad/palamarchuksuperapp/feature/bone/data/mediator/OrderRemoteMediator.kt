package com.hfad.palamarchuksuperapp.feature.bone.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.datastore.SyncPreferences
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.PrefetchRepository
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import java.util.Calendar

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val database: BoneDatabase,
    private val orderApi: OrderApi,
    private val status: OrderStatus?,
    private val syncPreferences: SyncPreferences,
    private val prefetchRepository: PrefetchRepository
) : RemoteMediator<Int, OrderEntityWithServices>() {

    val orderDao = database.orderDao()
    val remoteKeysDao = database.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val elevenAMTodayMillis = calendar.timeInMillis

        val lastSyncTime = syncPreferences.getLastSyncTime(status)

        val needsRefresh = lastSyncTime == null || lastSyncTime < elevenAMTodayMillis
        val localInvalid = orderDao.getOrdersWithServices(status).invalid

        return if (needsRefresh || localInvalid) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OrderEntityWithServices>,
    ): MediatorResult {
        if (!prefetchRepository.fetchDone.value) return MediatorResult.Success(endOfPaginationReached = false)

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) return MediatorResult.Success(endOfPaginationReached = true)
                val keys = database.remoteKeysDao().remoteKeysOrderId(lastItem.order.id, status)
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val response = orderApi.getOrdersByPage(page, state.config.pageSize, status)
            val endReached = response.size < state.config.pageSize

            database.withTransaction {
                val keys = response.map {
                    OrderRemoteKeys(
                        id = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1,
                        filter = status
                    )
                }
                if (loadType == LoadType.REFRESH) {
                    orderDao.deleteOrdersByStatus(status)
                    remoteKeysDao.clearRemoteKeysByStatus(status)
                }
                orderDao.insertOrIgnoreOrders(response.map { it.toEntity() }) //TODO
                remoteKeysDao.insertAll(keys)
            }
            if (loadType == LoadType.REFRESH) {
                syncPreferences.setLastSyncTime(status, System.currentTimeMillis())
            }

            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}