package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import androidx.room.Dao
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import kotlinx.coroutines.flow.Flow

@Dao
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