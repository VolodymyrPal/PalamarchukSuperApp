package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

data class CashPaymentOrder(
    override val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    override val billingDate: Date,
    override val type: TransactionType = TransactionType.CREDIT,
    override val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 0f
    ),
) : TypedTransaction