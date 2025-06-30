package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSalesStatistics

data class SalesPageState(
    val salesItems: List<SaleOrder> = generateSaleOrderItems(),       //TODO for test
    val salesStatistics: SalesStatistics = generateSalesStatistics(), //TODO for test
)