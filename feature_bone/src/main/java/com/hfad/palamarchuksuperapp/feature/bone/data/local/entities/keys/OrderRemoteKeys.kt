package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus

@Entity(tableName = "remote_order_keys")
data class OrderRemoteKeys(
    @PrimaryKey val id: Int,
    val filter: OrderStatus?,
    val prevKey: Int?,
    val nextKey: Int?,
)