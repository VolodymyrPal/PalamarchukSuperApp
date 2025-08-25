package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateExchangeOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems
import java.util.Calendar

data class FinancePageState(
    val salesItems: List<TypedTransaction> = (generateOrderItems() + generateSaleOrderItems() + generateExchangeOrderItems() + generatePaymentOrderItems()).shuffled(),
    val financeStatistics: FinanceStatistics = FinanceStatistics(),
    val query: String = "",
    val loading: Boolean = false,
    val startDate: Calendar = Calendar.getInstance(),
    val endDate: Calendar = Calendar.getInstance(),
) : ScreenState