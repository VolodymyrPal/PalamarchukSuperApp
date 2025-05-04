package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.generateSamplePayments

data class PaymentPageState(
    val payments: List<PaymentOrder> = generateSamplePayments(), //TODO for test only
    val paymentStatistic: PaymentStatistic = PaymentStatistic(),
)