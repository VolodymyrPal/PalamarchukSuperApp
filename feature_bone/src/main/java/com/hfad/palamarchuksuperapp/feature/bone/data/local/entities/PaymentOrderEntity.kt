package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_PAYMENTS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity(tableName = DATABASE_PAYMENTS)
data class PaymentOrderEntity(
    @PrimaryKey
    val id: Int,
    val factory: String,
    val productType: String,
    val paymentDate: String,
    val dueDate: String,
    val status: PaymentStatus,
    val commission: Float = 0.0f,
    val transactionType: TransactionType,
    val billingDate: Date = Date(),
    @Embedded (prefix = "amount_")
    val amountCurrency: AmountCurrencyEntity = AmountCurrencyEntity(Currency.USD, 0.0f),
    val versionHash: String = "",
)