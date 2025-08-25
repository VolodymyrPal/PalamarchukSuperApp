package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_CASH_PAYMENTS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType

@Entity(tableName = DATABASE_CASH_PAYMENTS)
data class CashPaymentOrderEntity(
    @PrimaryKey
    val id: Int,
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreation: Long,
    val billingDate: Long,
    val transactionType: TransactionType = TransactionType.CREDIT,
    @Embedded (prefix = "amount_")
    val amountCurrency: AmountCurrencyEntity = AmountCurrencyEntity(Currency.USD, 0f),
    val versionHash: String = "",
)