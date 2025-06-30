package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generatePaymentStatistic

data class PaymentPageState(
    val payments: List<PaymentOrder> = generatePaymentOrderItems(), //TODO for test only
    val paymentStatistic: PaymentStatistic = generatePaymentStatistic(), //TODO for test only
)