package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics

data class SalesPageState(
    val salesStatistics: SalesStatistics = SalesStatistics(),
    val saleStatusFilter: SaleStatus? = null,
    val searchQuery: String = "",
) : ScreenState