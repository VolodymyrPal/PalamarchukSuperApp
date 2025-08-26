package com.hfad.palamarchuksuperapp.feature.bone.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.PaymentRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentOrderApi

@OptIn(ExperimentalPagingApi::class)
class PaymentRemoteMediator(
    private val paymentApi: PaymentOrderApi,
    private val database: BoneDatabase,
    private val status: List<PaymentStatus>,
) : RemoteMediator<Int, PaymentOrderEntity>() {
    val paymentDao = database.paymentOrderDao()
    val remoteKeysDao = database.remoteKeysDao()

    private val statusFilter: String = status.joinToString(",") { it.name }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH // TODO add logic for refresh
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentOrderEntity>,
    ): MediatorResult {
        Log.d("PaymentRemoteMediator", "loadType: $loadType") // TODO

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) return MediatorResult.Success(endOfPaginationReached = false)
                val keys = database.remoteKeysDao().remoteKeysPaymentId(lastItem.id, statusFilter)
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        Log.d("PaymentRemoteMediator", "Page: $page") // TODO

        try {
            val response = paymentApi.getPaymentsByPage(page, state.config.pageSize, status)
            val endReached = response.size < state.config.pageSize

            database.withTransaction {
                val keys = response.map {
                    PaymentRemoteKeys(
                        id = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1,
                        status = statusFilter,
                    )
                }
                if (loadType == LoadType.REFRESH) {
//                    paymentDao.deletePaymentsByStatus(status) //TODO for testing
//                    remoteKeysDao.clearPaymentRemoteKeysByStatus(status)
                }
                paymentDao.insertOrIgnorePayments(response.map { it.toEntity() })
                remoteKeysDao.insertAllPaymentKeys(keys)
            }

            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            Log.e("PaymentRemoteMediator", "Error loading payments", e)
            return MediatorResult.Error(e)
        }
    }
}
