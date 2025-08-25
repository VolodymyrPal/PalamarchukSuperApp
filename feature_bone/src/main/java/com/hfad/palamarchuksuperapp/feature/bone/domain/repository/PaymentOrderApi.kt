package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.PaymentOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic

interface PaymentOrderApi {
    suspend fun getPaymentsByPage(page: Int, size: Int, status: List<PaymentStatus>): List<PaymentOrderDto>
    suspend fun getPaymentOrder(id: Int): PaymentOrderDto?
    suspend fun getPaymentsWithRange(from: Long, to: Long): List<PaymentOrderDto>
    suspend fun getPaymentStatistics(): PaymentStatistic
}