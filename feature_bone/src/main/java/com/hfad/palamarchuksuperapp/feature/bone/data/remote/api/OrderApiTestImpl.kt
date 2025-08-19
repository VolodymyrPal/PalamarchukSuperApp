package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderStatisticsDto
import com.hfad.palamarchuksuperapp.feature.bone.data.toDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrderApi
import com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels.generateOrderStatistic
import kotlinx.coroutines.delay
import java.util.Date
import javax.inject.Inject

class OrderApiTestImpl @Inject constructor() : OrderApi {
    override suspend fun getOrdersByPage(
        page: Int,
        size: Int,
        status: List<OrderStatus>,
    ): List<OrderDto> {
        delay(1500) //TODO
        return generateOrderItems().map { it.toDto() }
    }

    override suspend fun getOrder(id: Int): OrderDto? = null
    override suspend fun getOrdersWithRange(from: Date, to: Date): List<OrderDto> = emptyList()
    override suspend fun getOrderStatistics(): OrderStatisticsDto {
        delay(1500)
        return generateOrderStatistic().toDto()
    }
}