package com.hfad.palamarchuksuperapp.feature.bone.domain.models

data class OrderStatistics(
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val inProgressOrders: Int = 0,
    val totalOrderWeight: Float = 0f,
)