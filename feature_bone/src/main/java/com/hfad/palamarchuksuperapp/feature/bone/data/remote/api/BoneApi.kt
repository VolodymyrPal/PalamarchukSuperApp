package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import java.util.Date

interface BoneApi {
    fun getTypeOrderById(key: String) : TypedTransaction? // key = "${type}:${id}",

    fun getOrdersByPage(page: Int): List<Order>
    fun syncOrders(cachedOrders: List<Order>): SyncResponse<Order>
    fun syncOrderStatistic(): OrderStatistic

    fun getSaleOrdersByPage(page: Int): List<SaleOrder>
    fun syncSaleOrders(cachedOrders: List<SaleOrder>): SyncResponse<SaleOrder>
    fun syncSaleStatistics(): SalesStatistics

    fun getPaymentOrdersByPage(page: Int): List<PaymentOrder>
    fun syncPaymentOrders(cachedOrders: List<PaymentOrder>): SyncResponse<PaymentOrder>
    fun syncPaymentStatistic(): PaymentStatistic

    fun getExchangesByPage(page: Int): List<ExchangeOrder>

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