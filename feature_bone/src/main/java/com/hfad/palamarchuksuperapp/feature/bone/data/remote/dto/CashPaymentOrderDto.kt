package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

data class CashPaymentOrderDto(
    val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    val billingDate: Date,
    val transactionType: TransactionType = TransactionType.CREDIT,
    val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 0f
    ),
    val versionHash: String = "",
)