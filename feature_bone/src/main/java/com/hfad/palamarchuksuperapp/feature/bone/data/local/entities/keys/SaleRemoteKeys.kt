package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus

@Entity(tableName = "remote_sale_keys")
data class SaleRemoteKeys(
    @PrimaryKey val id: Int,
    val filter: SaleStatus?,
    val prevKey: Int?,
    val nextKey: Int?,
)