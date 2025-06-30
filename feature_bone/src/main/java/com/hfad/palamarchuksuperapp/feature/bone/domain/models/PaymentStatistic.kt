package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import kotlin.random.Random

data class PaymentStatistic(
    val totalPayment: Int = 0,
    val totalReceiver: Int = 0,
    val paymentsByCurrency: List<AmountCurrency> = emptyList(),
    val daysToSend: Int = 1,
)

fun generatePaymentStatistic(): PaymentStatistic {
    return PaymentStatistic(
        totalPayment = Random.nextInt(10, 20),
        totalReceiver = Random.nextInt(1, 4),
        daysToSend = 1,
        paymentsByCurrency = listOf(
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
        )
    )
}