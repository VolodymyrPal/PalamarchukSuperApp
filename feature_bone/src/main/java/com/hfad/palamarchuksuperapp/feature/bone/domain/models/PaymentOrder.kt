package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date

data class PaymentOrder(
    override val id: Int,
    val amountCurrency: AmountCurrency,
    val factory: String,
    val productType: String,
    val batchInfo: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
    override val type: TransactionType = TransactionType.CREDIT,
    override val amount: Float = amountCurrency.amount,
    override val billingDate: Date = Date(),
) : TypedTransaction

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}