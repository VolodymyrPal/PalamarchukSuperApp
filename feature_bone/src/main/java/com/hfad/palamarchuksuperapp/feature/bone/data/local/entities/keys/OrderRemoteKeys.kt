package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOTE_KEYS_ORDER
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus

@Entity(tableName = DATABASE_REMOTE_KEYS_ORDER)
data class OrderRemoteKeys(
    @PrimaryKey val id: Int,
    val statusFilter: String,  // OrderStatus with list<OrderStatus>().joinToString(",") { it.name }
    val prevKey: Int?,
    val nextKey: Int?,
)