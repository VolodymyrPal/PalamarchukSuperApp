package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceTransaction
import java.util.Date

data class SaleOrder(
    override val id: Int,
    val productName: String,
    val cargoCategory: String,
    val quantity: Int, //Quantity of what?
    val price: Int,
    val totalAmount: Int,
    val vatAmount: Double,
    val customerName: String,
    val status: SaleStatus,
    val requestDate: String,
    val documentDate: String,
    val companyName: String,
    val commissionPercent: Double,
    val prepayment: Boolean,
    val order: Order? = null,
    override val amount: Float = 0f,
    override val billingDate: Date = Date(),
) : FinanceTransaction

enum class SaleStatus {
    CREATED, IN_PROGRESS, DOCUMENT_PROCEED, COMPLETED, CANCELED
}