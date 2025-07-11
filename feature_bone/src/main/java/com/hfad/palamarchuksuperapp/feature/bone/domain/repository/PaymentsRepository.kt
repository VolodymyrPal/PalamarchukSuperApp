package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    val payments: AppResult<Flow<List<PaymentOrder>>, AppError>
    val paymentStatistic: AppResult<Flow<PaymentStatistic>, AppError>
    suspend fun getPaymentById(id: Int): AppResult<PaymentOrder, AppError>
    suspend fun softRefreshPayments()
    suspend fun hardRefreshPayments()
}