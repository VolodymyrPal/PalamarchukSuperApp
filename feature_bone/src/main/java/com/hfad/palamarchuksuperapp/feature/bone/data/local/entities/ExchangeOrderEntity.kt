package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_EXCHANGES
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity(tableName = DATABASE_EXCHANGES)
data class ExchangeOrderEntity(
    @PrimaryKey
    val id: Int,
    @Embedded (prefix = "amountTo_")
    val amountToExchange: AmountCurrencyEntity = AmountCurrencyEntity(Currency.USD, 0f),
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Date,
    val transactionType: TransactionType = TransactionType.CREDIT,
    @Embedded (prefix = "amountFrom_")
    val amountFromExchange: AmountCurrencyEntity = AmountCurrencyEntity(Currency.USD, 0f),
    val billingDate: Date,
    val versionHash: String = "",
)
