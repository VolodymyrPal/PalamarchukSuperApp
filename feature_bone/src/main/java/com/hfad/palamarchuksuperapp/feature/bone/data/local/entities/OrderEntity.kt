package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType

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
    val arrivalDate: Long,
    val containerNumber: String = "",
    val departurePoint: String,
    val cargo: String,
    val manager: String,
    @Embedded(prefix = "amount_")
    val amountCurrency: AmountCurrencyEntity = AmountCurrencyEntity(Currency.USD, 0f),
    val billingDate: Long,
    val transactionType: TransactionType = TransactionType.DEBIT,
    val versionHash: String = "",
)