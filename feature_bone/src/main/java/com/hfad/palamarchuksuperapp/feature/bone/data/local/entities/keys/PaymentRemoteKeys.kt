package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatus

@Entity(tableName = "remote_payment_keys")
data class PaymentRemoteKeys(
    @PrimaryKey val id: Int,
    val filter: PaymentStatus?,
    val prevKey: Int?,
    val nextKey: Int?,
)