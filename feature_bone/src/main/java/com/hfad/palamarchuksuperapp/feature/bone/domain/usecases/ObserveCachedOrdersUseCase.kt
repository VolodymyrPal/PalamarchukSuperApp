package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import kotlinx.coroutines.flow.Flow

interface ObserveCachedOrdersUseCase {
    operator fun invoke() : AppResult<Flow<List<Order>>, AppError>
}