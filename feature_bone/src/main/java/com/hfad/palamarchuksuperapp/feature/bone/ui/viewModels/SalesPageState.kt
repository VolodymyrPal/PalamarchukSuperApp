package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems

data class SalesPageState(
    val salesItems: List<SaleOrder> = generateSaleOrderItems(),
    val salesStatistics: SalesStatistics = SalesStatistics(),
)