package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.SaleOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.toDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.SaleOrderApi
import kotlinx.coroutines.delay
import javax.inject.Inject

class SaleApiTestImpl @Inject constructor(

) : SaleOrderApi {
    override suspend fun getSalesByPage(
        page: Int,
        size: Int,
        status: List<SaleStatus>,
    ): List<SaleOrderDto> {
        delay(1500) //TODO
        return generateSaleOrderItems().map { it.toDto() }
    }

    override suspend fun getSaleOrder(id: Int): SaleOrderDto? = null
    override suspend fun getSalesWithRange(from: Long, to: Long): List<SaleOrderDto> =
        generateSaleOrderItems().map { it.toDto() }

    override suspend fun getSalesStatistics(): SalesStatistics {
        delay(1500)
        return generateSalesStatistics()
    }
}
