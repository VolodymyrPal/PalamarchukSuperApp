package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SalesRepositoryImpl @Inject constructor(

) : SalesRepository {
    override val saleOrders: AppResult<Flow<List<SaleOrder>>, AppError> =
        AppResult.Success(flow { })
    override val salesStatistics: AppResult<Flow<SalesStatistics>, AppError> =
        AppResult.Success(flow { })
}