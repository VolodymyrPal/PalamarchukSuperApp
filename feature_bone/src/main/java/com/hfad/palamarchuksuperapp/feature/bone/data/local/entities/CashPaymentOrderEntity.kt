package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_CASH_PAYMENTS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity(tableName = DATABASE_CASH_PAYMENTS)
data class CashPaymentOrderEntity(
    @PrimaryKey
    val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Date,
    val billingDate: Date,
    val transactionType: TransactionType = TransactionType.CREDIT,
    val amount : Float = 0f,
    val currency: Currency = Currency.USD,
    val versionHash: String = "",
)