package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

data class Payment(
    override val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    override val amount: Float = 0f,
    override val billingDate: Date = Date(),
    override val type: TransactionType = TransactionType.CREDIT,
) : TypedTransaction