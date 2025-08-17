package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency

data class PaymentStatisticDto(
    val totalPayment: Int = 0,
    val totalReceiver: Int = 0,
    val daysToSend: Int = 0,
    val paymentsByCurrencyJson: List<AmountCurrency> = emptyList()
)