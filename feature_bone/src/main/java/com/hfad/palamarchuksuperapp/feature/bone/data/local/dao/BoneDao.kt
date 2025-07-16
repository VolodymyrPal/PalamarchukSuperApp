package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BoneDao {
    // Orders
    val orders : Flow<List<Order>>
    val orderStatistic : Flow<OrderStatistic>
    fun ordersInRange(from: Date, to: Date) : Flow<List<Order>>
    suspend fun getOrderById(id: Int): Order?
    suspend fun geAllOrders(): List<Order>
    suspend fun insertOrIgnoreOrders(orders : List<Order>)
    suspend fun deleteAllOrders()
    suspend fun insertOrIgnoreOrderStatistic(statistic : OrderStatistic)

    // SaleOrders
    val saleOrders : Flow<List<SaleOrder>>
    val salesStatistics : Flow<SalesStatistics>
    suspend fun getSaleOrderById(id: Int): SaleOrder?
    suspend fun getAllSaleOrders(): List<SaleOrder>
    suspend fun insertOrIgnoreSaleOrders(orders : List<SaleOrder>)
    suspend fun deleteAllSaleOrders()
    suspend fun insertOrIgnoreSalesStatistics(statistics : SalesStatistics)

    // PaymentOrders
    val paymentOrders : Flow<List<PaymentOrder>>
    val paymentStatistics: Flow<PaymentStatistic>
    suspend fun getPaymentOrderById(id: Int): PaymentOrder?
    suspend fun getAllPaymentOrders(): List<PaymentOrder>
    suspend fun insertOrIgnorePaymentOrders(orders : List<PaymentOrder>)
    suspend fun deleteAllPaymentOrders()
    suspend fun insertOrIgnorePaymentStatistics(statistics : List<PaymentStatistic>)

    // Finance Operations
    fun operationsInRange(from: Date, to: Date) : Flow<List<TypedTransaction>>
    val operations : Flow<List<TypedTransaction>>
    val financeStatistic : Flow<FinanceStatistic>

    // Exchanges
    val exchanges : Flow<List<ExchangeOrder>>
    suspend fun getExchangeById(id: Int): ExchangeOrder?
    suspend fun getAllExchangeOrders(): List<ExchangeOrder>
    suspend fun insertOrIgnoreExchanges(exchanges : List<ExchangeOrder>)
    suspend fun deleteAllExchanges()

    // General
    suspend fun clearAllTables()
}