package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import kotlinx.coroutines.flow.Flow

interface CashRepository {
    val cashPaymentOrders: AppResult<Flow<List<PaymentOrder>>, AppError>
}