package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    val payment: AppResult<Flow<List<PaymentOrder>>, AppError>
    val paymentStatistic: AppResult<Flow<List<PaymentStatistic>>, AppError>
}