package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ObserveCachedOrdersUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCachedOrdersUseCaseImpl @Inject constructor(

) : ObserveCachedOrdersUseCase {
    override fun invoke(): AppResult<Flow<List<Order>>, AppError> {
        TODO("Not yet implemented")
    }

}