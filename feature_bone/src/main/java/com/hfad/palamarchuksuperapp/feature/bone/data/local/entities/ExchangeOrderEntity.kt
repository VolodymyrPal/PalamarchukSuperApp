package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_EXCHANGES
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity (tableName = DATABASE_EXCHANGES)
data class ExchangeOrderEntity(
    @PrimaryKey
    val id: Int,
    val amountToExchange: AmountCurrency,
    val sumToExchange: Float,
    val currencyToChange: Currency,
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Date,
    val transactionType: TransactionType = TransactionType.CREDIT,
    val exchangedSum: Float,
    val exchangedCurrency: Currency,
    val billingDate: Date,
    val versionHash: String = ""
)
