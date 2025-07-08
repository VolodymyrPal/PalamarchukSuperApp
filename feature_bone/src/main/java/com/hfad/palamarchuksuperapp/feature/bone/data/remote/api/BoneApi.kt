package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder

interface BoneApi {
    fun getPaymentOrdersApi() : List<PaymentOrder>
}