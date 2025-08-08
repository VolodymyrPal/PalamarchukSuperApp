package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.SyncOrdersUseCase

class SyncOrdersUseCaseImpl // @Inject constructor
    (
    private val boneApi: OrderApi,
    private val boneControllerDao: OrderDao,
) : SyncOrdersUseCase {
    override suspend fun invoke(): AppResult<Unit, AppError> {
        return AppResult.Error(AppError.CustomError("Not implemented"))
    }

    override suspend fun invoke(list: List<Order>): AppResult<Unit, AppError> {
        return syncOrders(list)
    }

    private fun syncOrders(cachedOrders: List<Order>): AppResult<Unit, AppError> {
        return AppResult.Error(AppError.CustomError("Not implemented"))
    }
}