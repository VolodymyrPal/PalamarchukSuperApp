package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderStatisticsDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus

interface OrderApi {
    suspend fun getOrdersByPage(page: Int, size: Int, status: List<OrderStatus>): List<OrderDto>
    suspend fun getOrder(id: Int): OrderDto?
    suspend fun getOrdersWithRange(from: Long, to: Long): List<OrderDto>
    suspend fun getOrderStatistics(): OrderStatisticsDto
}