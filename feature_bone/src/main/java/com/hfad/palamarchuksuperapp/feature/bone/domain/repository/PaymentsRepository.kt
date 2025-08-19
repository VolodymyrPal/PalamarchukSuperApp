package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import androidx.paging.PagingData
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface PaymentsRepository {
    suspend fun paymentsInRange(from: Date, to: Date): AppResult<List<PaymentOrder>, AppError>
    suspend fun getPaymentById(id: Int): AppResult<PaymentOrder?, AppError>
    suspend fun refreshStatistic(): AppResult<PaymentStatistic, AppError>

    val paymentStatistics: Flow<AppResult<PaymentStatistic, AppError>>
    fun pagingPayments(status: List<PaymentStatus>, query: String): Flow<PagingData<PaymentOrder>>
}