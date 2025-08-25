package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.SaleOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics

interface SaleOrderApi {
    suspend fun getSalesByPage(page: Int, size: Int, status: List<SaleStatus>): List<SaleOrderDto>
    suspend fun getSaleOrder(id: Int): SaleOrderDto?
    suspend fun getSalesWithRange(from: Long, to: Long): List<SaleOrderDto>
    suspend fun getSalesStatistics(): SalesStatistics
}