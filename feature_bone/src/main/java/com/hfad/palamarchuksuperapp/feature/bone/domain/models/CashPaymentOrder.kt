package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

data class CashPaymentOrder(
    override val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    override val billingDate: Date,
    override val transactionType: TransactionType = TransactionType.CREDIT,
    override val amountCurrency: AmountCurrency = AmountCurrency(
        currency = Currency.USD,
        amount = 0f
    ),
    override val versionHash: String = ""
) : TypedTransaction {
    override val type: String = getType()
}