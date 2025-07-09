package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction

interface BoneApi {
    fun getOrdersApi(): List<Order>
    fun getOrderById(id: Int): Order
    fun getOrderStatisticApi(): OrderStatistic

    fun getSaleOrdersApi(): List<SaleOrder>
    fun getSaleOrderById(id: Int): SaleOrder
    fun getSalesStatisticsApi(): SalesStatistics

    fun getPaymentOrdersApi(): List<PaymentOrder>
    fun getPaymentById(id: Int): PaymentOrder
    fun getPaymentStatisticApi(): PaymentStatistic

    fun getOperationsApi(): List<TypedTransaction>
    fun getFinanceStatisticApi(): FinanceStatistic

    fun getExchangesApi(): List<ExchangeOrder>
    fun getExchangeById(id: Int): ExchangeOrder
}