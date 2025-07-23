package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

data class ExchangeOrderDto(
    val amountToExchange: AmountCurrency,
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Date,
    val transactionType: TransactionType = TransactionType.CREDIT,
    val billingDate: Date,
    val id: Int,
    val amountCurrency: AmountCurrency,
    val versionHash: String = "",
) {
    val exchangeRate: Float get() = amountToExchange.amount / amountCurrency.amount
}