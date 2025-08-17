package com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto

data class OrderStatisticsDto(
    val totalOrders: Int = 0,
    val inProgressOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalOrderWeight: Float = 0f,
)
