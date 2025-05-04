package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class PaymentStatistic(
    val totalPayment: Int = 0,
    val totalReceiver: Int = 0,
    val paymentsByCurrency: List<AmountCurrency> = listOf( //TODO test only
        AmountCurrency(
            currency = Currency.USD,
            amount = 24445f
        ),
        AmountCurrency(
            currency = Currency.EUR,
            amount = 335000f
        ),
        AmountCurrency(
            currency = Currency.BTC,
            amount = 220544.45f
        ),
        AmountCurrency(
            currency = Currency.UAH,
            amount = 8650.5f
        ),
        AmountCurrency(
            currency = Currency.CNY,
            amount = 8650.5f
        ),
        AmountCurrency(
            currency = Currency.PLN,
            amount = 9500f
        ),
        AmountCurrency(
            currency = Currency.OTHER,
            amount = 8882f,
        )
    ),
    val daysToSend: Int = 1,
)