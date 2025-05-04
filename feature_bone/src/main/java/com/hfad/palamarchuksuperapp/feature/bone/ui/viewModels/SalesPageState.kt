package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.generateSampleProductSaleItems

data class SalesPageState(
    val salesItems: List<SaleOrder> = generateSampleProductSaleItems(),
    val salesStatistics: SalesStatistics = SalesStatistics(),
)