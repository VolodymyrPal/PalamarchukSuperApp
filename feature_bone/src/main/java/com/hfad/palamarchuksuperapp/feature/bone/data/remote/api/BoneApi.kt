package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
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
    fun getTypeOrderById(key: String): TypedTransaction? // key = "${type}:${id}",

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

interface OrderApi {
    fun getOrdersByPage(page: Int): AppResult<List<Order>, AppError>
    fun getOrder(id: Int): AppResult<Order, AppError>
    fun getOrdersWithRange(from: Date, to: Date): AppResult<List<Order>, AppError>
    fun syncOrderStatistic(): AppResult<OrderStatistics, AppError>
}

interface SaleOrderApi {
    fun getSaleOrdersByPage(page: Int): AppResult<List<SaleOrder>, AppError>
    fun getSaleOrder(id: Int): AppResult<SaleOrder, AppError>
    fun getSaleOrdersWithRange(from: Date, to: Date): AppResult<List<Order>, AppError>
    fun syncSaleStatistics(): AppResult<SalesStatistics, AppError>
}

interface PaymentOrderApi {
    fun getPaymentOrdersByPage(page: Int): AppResult<List<PaymentOrder>, AppError>
    fun getPaymentOrder(id: Int): AppResult<PaymentOrder, AppError>
    fun getPaymentOrdersWithRange(from: Date, to: Date): AppResult<List<Order>, AppError>
    fun syncPaymentStatistic(): AppResult<PaymentStatistic, AppError>
}

interface ExchangeOrderApi {
    fun getExchangesByPage(page: Int): AppResult<List<ExchangeOrder>, AppError>
    fun getExchange(id: Int): AppResult<ExchangeOrder, AppError>
    fun getExchangesWithRange(from: Date, to: Date): AppResult<List<ExchangeOrder>, AppError>
}

interface TransactionSyncApi {
    fun getTypeOrderById(key: String): AppResult<TypedTransaction?, AppError>

    fun syncTypedTransactionsInRange(
        from: Date,
        to: Date,
        transactionHashesByTypeAndId: Map<String, String>,
    ): AppResult<SyncResponse<TypedTransaction>, AppError>
}


data class SyncResponse<T>(
    val new: List<T>,
    val updated: List<T>,
    val deleted: List<String>,
)