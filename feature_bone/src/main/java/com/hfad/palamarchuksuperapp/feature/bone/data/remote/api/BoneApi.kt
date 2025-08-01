package com.hfad.palamarchuksuperapp.feature.bone.data.remote.api

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import java.util.Date

interface OrderApi {
    fun getOrdersByPage(page: Int, size: Int, status: OrderStatus?): List<OrderDto>
    fun getOrder(id: Int): OrderDto?
    fun getOrdersWithRange(from: Date, to: Date): List<OrderDto>
    fun getOrderStatistics(): OrderStatistics
}

interface SaleOrderApi {
    fun getSaleOrdersByPage(page: Int): AppResult<List<SaleOrder>, AppError.NetworkException>
    fun getSaleOrder(id: Int): AppResult<SaleOrder, AppError.NetworkException>
    fun getSaleOrdersWithRange(from: Date, to: Date): AppResult<List<Order>, AppError.NetworkException>
    fun syncSaleStatistics(): AppResult<SalesStatistics, AppError.NetworkException>
}

interface PaymentOrderApi {
    fun getPaymentOrdersByPage(page: Int): AppResult<List<PaymentOrder>, AppError.NetworkException>
    fun getPaymentOrder(id: Int): AppResult<PaymentOrder, AppError.NetworkException>
    fun getPaymentOrdersWithRange(from: Date, to: Date): AppResult<List<Order>, AppError.NetworkException>
    fun syncPaymentStatistic(): AppResult<PaymentStatistic, AppError.NetworkException>
}

interface ExchangeOrderApi {
    fun getExchangesByPage(page: Int): AppResult<List<ExchangeOrder>, AppError.NetworkException>
    fun getExchange(id: Int): AppResult<ExchangeOrder, AppError.NetworkException>
    fun getExchangesWithRange(from: Date, to: Date): AppResult<List<ExchangeOrder>, AppError.NetworkException>
}

interface TransactionSyncApi {
    fun getTypeOrderById(key: String): AppResult<TypedTransaction?, AppError.NetworkException>

    fun syncTypedTransactionsInRange(
        from: Date,
        to: Date,
        transactionHashesByTypeAndId: Map<String, String>,
    ): AppResult<SyncResponse<TypedTransaction>, AppError.NetworkException>
}


data class SyncResponse<T>(
    val new: List<T>,
    val updated: List<T>,
    val deleted: List<String>,
)