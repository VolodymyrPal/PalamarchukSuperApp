package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateExchangeOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateSaleOrderItems

data class FinancePageState(
    val salesItems: List<TypedTransaction> = (generateOrderItems() + generateSaleOrderItems() + generateExchangeOrderItems() + generatePaymentOrderItems()).shuffled(),
    val financeStatistic: FinanceStatistic = FinanceStatistic(),
)