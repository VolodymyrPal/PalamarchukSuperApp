package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(

) : OrdersRepository {
    override val orderStatistic: AppResult<Flow<OrderStatistic>, AppError> = AppResult.Success(flow {  })
    override val orders: AppResult<Flow<List<Order>>, AppError> = AppResult.Success(flow {  })
}