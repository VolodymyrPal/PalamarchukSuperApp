package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics

data class OrderPageState(
    val orderMetrics: OrderStatistics = OrderStatistics(), // TODO for testing
) : ScreenState