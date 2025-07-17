package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems

data class OrderPageState(
    val orderMetrics: OrderStatistics = generateOrderStatistic(),// OrderStatistic(), TODO for testing
    val orders: List<Order> = generateOrderItems(),// emptyList(), TODO for testing
) : ScreenState