package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import java.util.Date

interface SaleOrderApi {
    fun getSaleOrdersByPage(page: Int): AppResult<List<SaleOrder>, AppError.NetworkException>
    fun getSaleOrder(id: Int): AppResult<SaleOrder, AppError.NetworkException>
    fun getSaleOrdersWithRange(
        from: Date,
        to: Date,
    ): AppResult<List<Order>, AppError.NetworkException>

    fun syncSaleStatistics(): AppResult<SalesStatistics, AppError.NetworkException>
}