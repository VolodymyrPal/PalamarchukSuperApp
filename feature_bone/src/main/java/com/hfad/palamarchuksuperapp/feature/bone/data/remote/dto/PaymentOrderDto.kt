package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

data class PaymentOrderDto(
    val id: Int,
    val factory: String,
    val productType: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
    val commission: Float = 0.0f,
    val transactionType: TransactionType,
    val billingDate: Long = Date().time,
    val amountCurrency: AmountCurrency,
    val paymentPrice: AmountCurrency = AmountCurrency(
        currency = amountCurrency.currency,
        amount = 100f
    ),
    val versionHash: String = ""
)