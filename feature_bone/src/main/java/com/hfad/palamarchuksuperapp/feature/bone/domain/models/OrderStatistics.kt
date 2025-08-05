package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_STATISTICS

@Entity (
    tableName = DATABASE_STATISTICS,
)
data class OrderStatistics(
    @PrimaryKey
    val id: Int = 1,
    val totalOrders: Int = 0,
    val completedOrders: Int = 0,
    val inProgressOrders: Int = 0,
    val totalOrderWeight: Float = 0f,
)