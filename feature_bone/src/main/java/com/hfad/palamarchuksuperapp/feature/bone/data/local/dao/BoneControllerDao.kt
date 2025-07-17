package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.FinanceStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TypedTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BoneControllerDao : OrderDao, SaleOrderDao, PaymentOrderDao, ExchangeOrderDao, GeneralDao, FinanceOperationDao {
    // Orders
    override val cachedOrders : Flow<List<Order>>
    override val cachedOrderStatistics : Flow<OrderStatistics>
    override fun ordersInRange(from: Date, to: Date) : Flow<List<Order>>
    override suspend fun getOrderById(id: Int): Flow<Order?>
    override suspend fun geAllOrders(): List<Order>
    override suspend fun insertOrIgnoreOrders(orders : List<Order>)
    override suspend fun deleteAllOrders()
    override suspend fun clearOrderStatistics()
    override suspend fun insertOrIgnoreOrderStatistic(statistic : OrderStatistics)

    // SaleOrders
    override val saleOrders : Flow<List<SaleOrder>>
    override val salesStatistics : Flow<SalesStatistics>
    override suspend fun getSaleOrderById(id: Int): SaleOrder?
    override suspend fun getAllSaleOrders(): List<SaleOrder>
    override suspend fun insertOrIgnoreSaleOrders(orders : List<SaleOrder>)
    override suspend fun deleteAllSaleOrders()
    override suspend fun clearSalesStatistics()
    override suspend fun insertOrIgnoreSalesStatistics(statistics : SalesStatistics)

    // PaymentOrders
    override val paymentOrders : Flow<List<PaymentOrder>>
    override val paymentStatistics: Flow<PaymentStatistic>
    override suspend fun getPaymentOrderById(id: Int): PaymentOrder?
    override suspend fun getAllPaymentOrders(): List<PaymentOrder>
    override suspend fun insertOrIgnorePaymentOrders(orders : List<PaymentOrder>)
    override suspend fun deleteAllPaymentOrders()
    override suspend fun clearPaymentStatistics()
    override suspend fun insertOrIgnorePaymentStatistics(statistics : List<PaymentStatistic>)

    // Finance Operations
    override fun operationsInRange(from: Date, to: Date) : Flow<List<TypedTransaction>>
    override val operations : Flow<List<TypedTransaction>>
    override val financeStatistics : Flow<FinanceStatistics>

    // Exchanges
    override val exchanges : Flow<List<ExchangeOrder>>
    override suspend fun getExchangeById(id: Int): ExchangeOrder?
    override suspend fun getAllExchangeOrders(): List<ExchangeOrder>
    override suspend fun insertOrIgnoreExchanges(exchanges : List<ExchangeOrder>)
    override suspend fun deleteAllExchanges()

    // General
    override suspend fun clearAllTables()
}

interface OrderDao {
    val cachedOrders : Flow<List<Order>>
    val cachedOrderStatistics : Flow<OrderStatistics>
    fun ordersInRange(from: Date, to: Date) : Flow<List<Order>>
    suspend fun getOrderById(id: Int): Flow<Order?>
    suspend fun geAllOrders(): List<Order>
    suspend fun insertOrIgnoreOrders(orders : List<Order>)
    suspend fun deleteAllOrders()

    suspend fun clearOrderStatistics()
    suspend fun insertOrIgnoreOrderStatistic(statistic : OrderStatistics)
}

interface SaleOrderDao {
    val saleOrders : Flow<List<SaleOrder>>
    val salesStatistics : Flow<SalesStatistics>
    suspend fun getSaleOrderById(id: Int): SaleOrder?
    suspend fun getAllSaleOrders(): List<SaleOrder>
    suspend fun insertOrIgnoreSaleOrders(orders : List<SaleOrder>)
    suspend fun deleteAllSaleOrders()
    suspend fun clearSalesStatistics()
    suspend fun insertOrIgnoreSalesStatistics(statistics : SalesStatistics)
}

interface PaymentOrderDao {
    val paymentOrders : Flow<List<PaymentOrder>>
    val paymentStatistics: Flow<PaymentStatistic>
    suspend fun getPaymentOrderById(id: Int): PaymentOrder?

    suspend fun getAllPaymentOrders(): List<PaymentOrder>
    suspend fun insertOrIgnorePaymentOrders(orders : List<PaymentOrder>)
    suspend fun deleteAllPaymentOrders()
    suspend fun clearPaymentStatistics()
    suspend fun insertOrIgnorePaymentStatistics(statistics : List<PaymentStatistic>)
}

interface FinanceOperationDao {
    fun operationsInRange(from: Date, to: Date) : Flow<List<TypedTransaction>>
    val operations : Flow<List<TypedTransaction>>
    val financeStatistics : Flow<FinanceStatistics>
}

interface ExchangeOrderDao {
    val exchanges : Flow<List<ExchangeOrder>>
    suspend fun getExchangeById(id: Int): ExchangeOrder?
    suspend fun getAllExchangeOrders(): List<ExchangeOrder>
    suspend fun insertOrIgnoreExchanges(exchanges : List<ExchangeOrder>)
    suspend fun deleteAllExchanges()
}

interface GeneralDao {
    suspend fun clearAllTables()
}