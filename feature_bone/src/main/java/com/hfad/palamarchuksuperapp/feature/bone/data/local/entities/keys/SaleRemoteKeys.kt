package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOVE_KEYS_SALES
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus

@Entity(tableName = DATABASE_REMOVE_KEYS_SALES)
data class SaleRemoteKeys(
    @PrimaryKey val id: Int,
    val status: SaleStatus?,
    val prevKey: Int?,
    val nextKey: Int?,
)