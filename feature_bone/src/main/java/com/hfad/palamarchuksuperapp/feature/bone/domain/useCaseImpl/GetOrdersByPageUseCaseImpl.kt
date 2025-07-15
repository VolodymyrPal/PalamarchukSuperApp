package com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.GetOrdersByPageUseCase
import javax.inject.Inject

class GetOrdersByPageUseCaseImpl @Inject constructor(

) : GetOrdersByPageUseCase {
    override fun invoke(page: Int): AppResult<List<Order>, AppError> {
        TODO("Not yet implemented")
    }
}