package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.State
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems

data class OrderPageState(
    val orderMetrics: OrderStatistic = generateOrderStatistic(),// OrderStatistic(), TODO for testing
    val orders: List<Order> = generateOrderItems(),// emptyList(), TODO for testing
) : State<Order>