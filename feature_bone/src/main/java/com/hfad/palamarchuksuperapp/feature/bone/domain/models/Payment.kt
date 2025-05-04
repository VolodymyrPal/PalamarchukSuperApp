package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceTransaction
import java.util.Date

data class Payment(
    override val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    override val amount: Float = 0f,
    override val billingDate: Date = Date(),
) : FinanceTransaction