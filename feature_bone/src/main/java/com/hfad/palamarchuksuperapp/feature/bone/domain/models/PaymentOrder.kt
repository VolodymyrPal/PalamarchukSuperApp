package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class PaymentOrder(
    val id: Int,
    val amountCurrency: AmountCurrency,
    val factory: String,
    val productType: String,
    val batchInfo: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}