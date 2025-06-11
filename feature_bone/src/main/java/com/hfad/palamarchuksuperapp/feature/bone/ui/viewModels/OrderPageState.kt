package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.State
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic

data class OrderPageState(
    val orderMetrics: OrderStatistic = OrderStatistic(),
    val orders: List<Order> = emptyList(),
) : State<Order>