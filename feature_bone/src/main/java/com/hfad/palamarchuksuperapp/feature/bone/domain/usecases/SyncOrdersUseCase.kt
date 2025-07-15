package com.hfad.palamarchuksuperapp.feature.bone.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order

interface SyncOrdersUseCase {
    suspend operator fun invoke() : AppResult<Unit, AppError>
    suspend operator fun invoke(list: List<Order>) : AppResult<Unit, AppError>
}