package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import java.util.Date
import kotlin.random.Random

data class ExchangeOrder(
    val amountToExchange: AmountCurrency,
    val typeToChange: TransactionType = TransactionType.DEBIT,
    val date: Date,
    override val transactionType: TransactionType = TransactionType.CREDIT,
    override val amountCurrency: AmountCurrency,
    override val billingDate: Date,
    override val id: Int,
    override val versionHash: String = "",
) : TypedTransaction {
    override val type: String = getType()
    val exchangeRate: Float get() = amountToExchange.amount / amountCurrency.amount
}

fun generateExchangeOrderItems(): List<ExchangeOrder> {
    return listOf(
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 41000f, currency = Currency.UAH),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 1000f, currency = Currency.USD),
            billingDate = Date(),
            id = Random.nextInt(1, 100000),
            transactionType = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 1f, currency = Currency.BTC),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 80000f, currency = Currency.USD),
            billingDate = Date(),
            id = Random.nextInt(1, 100000),
            transactionType = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 27349.85f, currency = Currency.USD),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 24500f, currency = Currency.EUR),
            billingDate = Date(),
            id = Random.nextInt(1, 100000),
            transactionType = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
        ExchangeOrder(
            amountToExchange = AmountCurrency(amount = 11000f, currency = Currency.CNY),
            date = Date(),
            amountCurrency = AmountCurrency(amount = 14500f, currency = Currency.PLN),
            billingDate = Date(),
            id = Random.nextInt(1, 100000),
            transactionType = TransactionType.CREDIT,
            typeToChange = TransactionType.DEBIT
        ),
    )
}