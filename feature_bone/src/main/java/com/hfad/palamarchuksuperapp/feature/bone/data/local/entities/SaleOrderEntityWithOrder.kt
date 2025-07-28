package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SaleOrderEntityWithOrder (
    @Embedded
    val saleOrder: SaleOrderEntity,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "id"
    )
    val order: OrderEntity?
)