package com.hfad.palamarchuksuperapp.feature.bone.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.BoneDatabase
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.SaleRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SaleOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.toEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SaleOrderApi

@OptIn(ExperimentalPagingApi::class)
class SaleRemoteMediator(
    private val saleApi: SaleOrderApi,
    private val database: BoneDatabase,
    private val status: SaleStatus?,
) : RemoteMediator<Int, SaleOrderEntity>() {

    val saleDao = database.saleOrderDao()
    val remoteKeysDao = database.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH //TODO better refresh logic
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SaleOrderEntity>,
    ): MediatorResult {

        Log.d("SaleRemoteMediator", "loadType: $loadType")

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) return MediatorResult.Success(endOfPaginationReached = false)
                val keys = database.remoteKeysDao().remoteKeysSaleId(lastItem.id, status)
                keys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        Log.d("SaleRemoteMediator", "Page: $page")

        try {
            val response = saleApi.getSalesByPage(page, state.config.pageSize, status)
            val endReached = response.size < state.config.pageSize

            database.withTransaction {
                val keys = response.map {
                    SaleRemoteKeys(
                        id = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1,
                        filter = status
                    )
                }
                if (loadType == LoadType.REFRESH) {
//                    saleDao.deleteSalesByStatus(status) //TODO for testing
//                    remoteKeysDao.clearSaleRemoteKeysByStatus(status)
                }
                saleDao.insertOrIgnoreSales(response.map { it.toEntity() })
                remoteKeysDao.insertAllSaleKeys(keys)
            }

            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            Log.e("SaleRemoteMediator", "Error loading sales", e)
            return MediatorResult.Error(e)
        }
    }
}
