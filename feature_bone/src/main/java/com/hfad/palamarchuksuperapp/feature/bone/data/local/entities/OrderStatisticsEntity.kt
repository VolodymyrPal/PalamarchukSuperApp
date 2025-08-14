package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDER_STATISTICS

@Entity(tableName = DATABASE_ORDER_STATISTICS)
data class OrderStatisticsEntity(
    @PrimaryKey
    val id: Int = 1,
    val inProgressOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalOrderWeight: Float = 0f,
)
