package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems

data class FinancePageState(
    val salesItems: List<TypedTransaction> = generateOrderItems(),
    val salesStatistics: FinanceStatistic = FinanceStatistic(),
)