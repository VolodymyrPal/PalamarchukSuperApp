package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order

data class FinancePageState(
    val salesItems: List<FinanceTransaction> = listOf(
        Order(1),
        Order(2),
        Order(3),
    ),
    val salesStatistics: FinanceStatistic = FinanceStatistic(),
)