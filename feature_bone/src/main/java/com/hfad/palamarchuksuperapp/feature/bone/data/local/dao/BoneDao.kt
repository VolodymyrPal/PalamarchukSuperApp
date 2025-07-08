package com.hfad.palamarchuksuperapp.feature.bone.data.local.dao

import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import kotlinx.coroutines.flow.Flow

interface BoneDao {
    val getPaymentOrders : Flow<List<PaymentOrder>>
    suspend fun insertOrIgnorePaymentOrders(orders : List<PaymentOrder>)
    suspend fun deleteAllPaymentOrders()
}