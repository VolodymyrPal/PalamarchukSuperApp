package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity(
    tableName = DATABASE_ORDERS,
    indices = [
        Index(value = ["status"]),
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: Int,
    val businessEntityNum: Int,
    val num: Int,
    val status: OrderStatus = OrderStatus.CREATED,
    val destinationPoint: String,
    val arrivalDate: Date,
    val containerNumber: String = "",
    val departurePoint: String,
    val cargo: String,
    val manager: String,
    val sum: Float = 0f,
    val currency: Currency = Currency.USD,
    val billingDate: Date,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val versionHash: String = "",
)