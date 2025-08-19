package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic

data class PaymentsPageState(
    val paymentStatistic: PaymentStatistic = PaymentStatistic(),
    val paymentStatusFilter: List<PaymentStatus> = emptyList(),
    val searchQuery: String = "",
) : ScreenState