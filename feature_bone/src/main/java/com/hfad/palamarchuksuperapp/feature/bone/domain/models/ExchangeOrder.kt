package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date
import kotlin.random.Random

data class ExchangeOrder(
    val amountToExchange: AmountCurrency,
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Date,
    val exchangeRate: Float,
    override val type: TransactionType = TransactionType.CREDIT,
    override val amountCurrency: AmountCurrency,
    override val billingDate: Date,
    override val id: Int,
) : TypedTransaction

fun generateExchangeOrderItems(): List<ExchangeOrder> {
    return listOf(
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 41000f, currency = Currency.UAH),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 1000f, currency = Currency.USD),
            billingDate = Date(),
            exchangeRate = 41f,
            id = Random.nextInt(),
            type = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 1f, currency = Currency.BTC),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 80000f, currency = Currency.USD),
            billingDate = Date(),
            exchangeRate = 80000f,
            id = Random.nextInt(),
            type = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
    )
}