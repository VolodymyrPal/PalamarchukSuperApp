package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SERVICE_ORDERS
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus

@Entity (
    tableName = DATABASE_SERVICE_ORDERS,
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class ServiceOrderEntity(
    @PrimaryKey
    val id: Int = 0,
    val orderId: Int? = null,
    val fullTransport: Boolean = true,
    val serviceType: ServiceType = ServiceType.OTHER,
    val price: Float = 0.0f,
    val durationDay: Int = 0,
    val status: StepperStatus = StepperStatus.CREATED,
)