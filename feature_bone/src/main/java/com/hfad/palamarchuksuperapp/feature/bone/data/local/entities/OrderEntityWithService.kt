package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithServices(
    @Embedded val order: OrderEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val services: List<ServiceOrderEntity>,
)