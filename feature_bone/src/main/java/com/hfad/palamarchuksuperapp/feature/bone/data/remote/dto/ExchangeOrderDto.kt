package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType

data class ExchangeOrderDto(
    val amountToExchange: AmountCurrency,
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Long,
    val transactionType: TransactionType = TransactionType.CREDIT,
    val billingDate: Long,
    val id: Int,
    val amountCurrency: AmountCurrency,
    val versionHash: String = "",
) {
    val exchangeRate: Float get() = amountToExchange.amount / amountCurrency.amount
}