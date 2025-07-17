package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import java.util.Date

interface BoneApi {
    fun getTypeOrderById(key: String) : TypedTransaction? // key = "${type}:${id}",

    fun getOrdersByPage(page: Int): List<Order>
    fun getOrder(id: Int): Order
    fun getOrdersWithRange(from: Date, to: Date): List<Order>
    fun syncOrderStatistic(): OrderStatistics

    fun getSaleOrdersByPage(page: Int): List<SaleOrder>
    fun getSaleOrder(id: Int): SaleOrder
    fun getSaleOrdersWithRange(from: Date, to: Date): List<Order>
    fun syncSaleStatistics(): SalesStatistics

    fun getPaymentOrdersByPage(page: Int): List<PaymentOrder>
    fun getPaymentOrder(id: Int): PaymentOrder
    fun getPaymentOrdersWithRange(from: Date, to: Date): List<Order>
    fun syncPaymentStatistic(): PaymentStatistic

    fun getExchangesByPage(page: Int): List<ExchangeOrder>
    fun getExchange(id: Int): ExchangeOrder
    fun getExchangesWithRange(from: Date, to: Date): List<ExchangeOrder>

    fun syncTypedTransactionsInRange(
        from: Date,
        to: Date,
        transactionHashesByTypeAndId: Map<String, String>, // key = "${type}:${id}", value = backand hash
    ): SyncResponse<TypedTransaction>
}

data class SyncResponse<T>(
    val new: List<T>,
    val updated: List<T>,
    val deleted: List<String>,
)