package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class SalesStatistics(
    val totalSalesAmount: AmountCurrency = AmountCurrency(
        currency = Currency.UAH,
        amount = 495000f
    ), //TODO test purpose
    val totalSalesNdsAmount: AmountCurrency = AmountCurrency(
        currency = Currency.UAH,
        amount = totalSalesAmount.amount * (20f / (100 + 20f))
    ), //TODO test purpose
    val totalBuyers: Int = 12, //TODO test purpose
)