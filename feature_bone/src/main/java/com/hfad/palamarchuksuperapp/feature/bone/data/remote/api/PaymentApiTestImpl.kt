package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.PaymentOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.toDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.PaymentOrderApi
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentStatistic
import kotlinx.coroutines.delay
import java.util.Date
import javax.inject.Inject

class PaymentApiTestImpl @Inject constructor(

) : PaymentOrderApi {
    override suspend fun getPaymentsByPage(
        page: Int,
        size: Int,
        status: List<PaymentStatus>,
    ): List<PaymentOrderDto> {
        delay(1500) //TODO
        return generatePaymentOrderItems().map { it.toDto() }
    }

    override suspend fun getPaymentOrder(id: Int): PaymentOrderDto? = null
    override suspend fun getPaymentsWithRange(from: Date, to: Date): List<PaymentOrderDto> = emptyList()
    override suspend fun getPaymentStatistics(): PaymentStatistic {
        delay(1500)
        return generatePaymentStatistic()
    }
}
