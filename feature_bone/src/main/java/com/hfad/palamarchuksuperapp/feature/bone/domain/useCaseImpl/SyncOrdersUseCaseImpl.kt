package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.BoneControllerDao
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.api.BoneApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.SyncOrdersUseCase
import javax.inject.Inject

class SyncOrdersUseCaseImpl @Inject constructor(
    private val boneApi: BoneApi,
    private val boneControllerDao: BoneControllerDao,
) : SyncOrdersUseCase {
    override suspend fun invoke(): AppResult<Unit, AppError> {
        val cachedDaoOrders = withSqlErrorHandling { boneControllerDao.geAllOrders() }
        val syncResult = when (cachedDaoOrders) {
            is AppResult.Error -> return AppResult.Error(cachedDaoOrders.error)
            is AppResult.Success -> {
                syncOrders(cachedDaoOrders.data)
            }
        }
        return if (syncResult is AppResult.Error)
            AppResult.Error(syncResult.error)
        else
            AppResult.Success(
                Unit
            )
    }

    override suspend fun invoke(list: List<Order>): AppResult<Unit, AppError> {
        return syncOrders(list)
    }

    private fun syncOrders(cachedOrders: List<Order>): AppResult<Unit, AppError> {
        return AppResult.Error(AppError.CustomError("Not implemented"))
    }
}