package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency

data class SalesStatisticsDto(
    val totalSalesAmount: AmountCurrency = AmountCurrency(currency = Currency.UAH, amount = 0f),
    val totalSalesNdsAmount: AmountCurrency = AmountCurrency(currency = Currency.UAH, amount = 0f),
    val totalBuyers: Int = 0,
)
