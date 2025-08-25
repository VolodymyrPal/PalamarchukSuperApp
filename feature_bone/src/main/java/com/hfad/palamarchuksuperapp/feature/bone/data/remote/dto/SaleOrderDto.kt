package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

data class SaleOrderDto(
    val id: Int,
    val productName: String,
    val cargoCategory: String,
    val customerName: String,
    val status: SaleStatus,
    val requestDate: String,
    val documentDate: String,
    val companyName: String,
    val commissionPercent: Double,
    val prepayment: Boolean,
    val order: Order? = null,
    val vat: Float = 0.20f,
    val amountCurrency: AmountCurrency,
    val billingDate: Long = Date().time,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val versionHash: String = ""
)