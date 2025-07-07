package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentsRepositoryImpl @Inject constructor(

) : PaymentsRepository {
    override val payment: AppResult<Flow<List<PaymentOrder>>, AppError> = AppResult.Success(flow {  })
    override val paymentStatistic: AppResult<Flow<List<PaymentStatistic>>, AppError> = AppResult.Success(flow {  })
}