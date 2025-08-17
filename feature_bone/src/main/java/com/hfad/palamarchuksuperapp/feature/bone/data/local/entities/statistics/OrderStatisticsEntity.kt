package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_ORDER_STATISTICS

@Entity(tableName = DATABASE_ORDER_STATISTICS)
data class OrderStatisticsEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalOrders: Int = 0,
    val inProgressOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalOrderWeight: Float = 0f,
)