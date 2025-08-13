package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import java.util.Date

interface PaymentOrderApi {
    fun getPaymentOrdersByPage(page: Int): AppResult<List<PaymentOrder>, AppError.NetworkException>
    fun getPaymentOrder(id: Int): AppResult<PaymentOrder, AppError.NetworkException>
    fun getPaymentOrdersWithRange(
        from: Date,
        to: Date,
    ): AppResult<List<Order>, AppError.NetworkException>

    fun syncPaymentStatistic(): AppResult<PaymentStatistic, AppError.NetworkException>
}