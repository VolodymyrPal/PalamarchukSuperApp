package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_PAYMENTS_STATISTICS

@Entity(tableName = DATABASE_PAYMENTS_STATISTICS)
data class PaymentStatisticEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalPayment: Int = 0,
    val totalReceiver: Int = 0,
    val daysToSend: Int = 0,
    val paymentsByCurrencyJson: String = "", // JSON string for List<AmountCurrency>
)
