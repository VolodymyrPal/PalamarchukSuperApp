package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import kotlinx.coroutines.flow.Flow

interface SalesRepository {
    val saleOrders: AppResult<Flow<List<SaleOrder>>, AppError>
    val salesStatistics : AppResult<Flow<SalesStatistics>, AppError>
}