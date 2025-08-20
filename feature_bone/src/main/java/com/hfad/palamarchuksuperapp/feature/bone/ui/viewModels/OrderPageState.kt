package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import java.util.Date

data class OrderPageState(
    val orderMetrics: OrderStatistics = OrderStatistics(), // TODO for testing
    val orderStatusFilter: List<OrderStatus> = emptyList(),
    val searchQuery: String = "",
    val dateRange: Pair<Date, Date> = Pair(Date(), Date()),
) : ScreenState