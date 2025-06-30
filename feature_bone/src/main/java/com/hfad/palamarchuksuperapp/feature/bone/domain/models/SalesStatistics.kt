package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class SalesStatistics(
    val totalSalesAmount: AmountCurrency = AmountCurrency(currency = Currency.UAH, amount = 0f),
    val totalSalesNdsAmount: AmountCurrency = AmountCurrency(currency = Currency.UAH, amount = 0f),
    val totalBuyers: Int = 0,
)

fun generateSalesStatistics(): SalesStatistics {
    val totalSalesAmount = AmountCurrency(
        currency = Currency.UAH,
        amount = 495000f
    )
    val totalSalesNdsAmount = AmountCurrency(
        currency = Currency.UAH,
        amount = totalSalesAmount.amount * (20f / (100 + 20f))
    )
    return SalesStatistics(
        totalSalesAmount = totalSalesAmount,
        totalSalesNdsAmount = totalSalesNdsAmount,
        totalBuyers = 12,
    )
}