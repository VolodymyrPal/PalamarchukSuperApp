package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_PAYMENTS_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.AmountCurrencyEntity

@Entity(tableName = DATABASE_PAYMENTS_STATISTICS)
data class PaymentStatisticEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalPayment: Int = 0,
    val totalReceiver: Int = 0,
    val daysToSend: Int = 0,
    val paymentsByCurrency: List<AmountCurrencyEntity> = emptyList(),
)
