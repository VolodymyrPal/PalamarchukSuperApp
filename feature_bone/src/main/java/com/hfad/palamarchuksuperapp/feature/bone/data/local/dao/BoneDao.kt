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

interface BoneDao {
    // Orders
    val orders: Flow<List<Order>>
    val orderStatistic: Flow<OrderStatistic>
    suspend fun getOrderById(id: Int): Order
    suspend fun deleteAllOrders()

    // SaleOrders
    val saleOrders: Flow<List<SaleOrder>>
    val salesStatistics: Flow<SalesStatistics>
    suspend fun getSaleOrderById(id: Int): SaleOrder
    suspend fun deleteAllSaleOrders()

    // PaymentOrders
    val paymentOrders: Flow<List<PaymentOrder>>
    val paymentStatistics: Flow<List<PaymentStatistic>>
    suspend fun getPaymentOrderById(id: Int): PaymentOrder
    suspend fun deleteAllPaymentOrders()

    // Finance Operations
    val operations: Flow<List<TypedTransaction>>
    val financeStatistic: Flow<FinanceStatistic>
    suspend fun getOperationById(id: Int): TypedTransaction
    suspend fun deleteAllOperations()

    // Exchanges
    val exchanges: Flow<List<ExchangeOrder>>
    suspend fun getExchangeById(id: Int): ExchangeOrder
    suspend fun deleteAllExchanges()
}