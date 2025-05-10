package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

data class PaymentOrder(
    override val id: Int,
    val factory: String,
    val productType: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
    override val type: TransactionType,
    override val billingDate: Date = Date(),
    override val amountCurrency: AmountCurrency,
) : TypedTransaction

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}