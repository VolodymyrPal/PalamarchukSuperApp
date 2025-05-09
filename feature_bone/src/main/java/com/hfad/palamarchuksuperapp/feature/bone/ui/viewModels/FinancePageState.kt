package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction

data class FinancePageState(
    val salesItems: List<TypedTransaction> = listOf(
        Order(1),
        Order(2),
        Order(3),
    ),
    val salesStatistics: FinanceStatistic = FinanceStatistic(),
)