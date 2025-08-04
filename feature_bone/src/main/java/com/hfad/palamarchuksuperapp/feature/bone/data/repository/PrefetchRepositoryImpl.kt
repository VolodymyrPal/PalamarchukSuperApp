package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.map
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.datastore.SyncPreferences
import com.hfad.palamarchuksuperapp.feature.bone.data.mediator.OrderRemoteMediator
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.data.toDomain
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PrefetchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class PrefetchRepositoryImpl @Inject constructor (
    private val boneDatabase: BoneDatabase,
    private val orderApi: OrderApi,
    private val syncPreferences: SyncPreferences,
) : PrefetchRepository {

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 11)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val elevenAMTodayMillis = calendar.timeInMillis

//    val lastSyncTime = syncPreferences.getLastSyncTime(status)
//
//    val needsRefresh = lastSyncTime == null || lastSyncTime < elevenAMTodayMillis
//    val localInvalid = orderDao.getOrdersWithServices(status).invalid
//
//    return if (needsRefresh || localInvalid) {
//        InitializeAction.LAUNCH_INITIAL_REFRESH
//    } else {
//        InitializeAction.SKIP_INITIAL_REFRESH
//    }
//
//    private val orderDao = boneDatabase.orderDao()
//
//    @OptIn(ExperimentalPagingApi::class)
//    private val pagerCache = mutableMapOf<OrderStatus?, Flow<PagingData<Order>>>()
//
//    @OptIn(ExperimentalPagingApi::class)
//    fun pagingOrders(status: OrderStatus?): Flow<PagingData<Order>> {
//
//    }
}