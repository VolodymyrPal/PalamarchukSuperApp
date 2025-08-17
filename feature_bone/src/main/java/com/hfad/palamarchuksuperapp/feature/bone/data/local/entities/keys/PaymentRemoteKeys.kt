package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_REMOVE_KEYS_PAYMENT
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus

@Entity(tableName = DATABASE_REMOVE_KEYS_PAYMENT)
data class PaymentRemoteKeys(
    @PrimaryKey val id: Int,
    val status: PaymentStatus?,
    val prevKey: Int?,
    val nextKey: Int?,
)