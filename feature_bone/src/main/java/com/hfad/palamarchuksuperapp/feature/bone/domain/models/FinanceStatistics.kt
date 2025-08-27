package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class FinanceStatistics(
    val totalSales: Float = 0f,
    val totalExpenses: Float = 0f,
    val totalProfit: Float = 0f,
    val paymentsList: List<AmountCurrency> = emptyList(),
)

fun generateFinanceStatistics(): FinanceStatistics = FinanceStatistics(
    totalSales = 40f,
    totalExpenses = 20f,
    totalProfit = 80000f,
    paymentsList = listOf(
        AmountCurrency(Currency.UAH, 725025.56f),
        AmountCurrency(Currency.USD, 200000f),
        AmountCurrency(Currency.EUR, 56350f),
        AmountCurrency(Currency.BTC, 0.00005f),
    ),
)
