package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.Stepper

@Suppress("LongParameterList")
@Stable
class ServiceOrder(
    /* PrimaryKey - ID */
    val id: Int = 0,
    val orderId: Int? = null,
    val fullTransport: Boolean = true,
    override val serviceType: ServiceType = ServiceType.OTHER,
    val price: Float = 0.0f,
    val durationDay: Int = 0,
    override val status: StepperStatus = StepperStatus.CREATED,
) : Stepper {
    @get: DrawableRes
    override val icon = when (serviceType) {
        ServiceType.FULL_FREIGHT -> R.drawable.in_progress
        ServiceType.AIR_FREIGHT -> R.drawable.kilogram
        ServiceType.CUSTOMS -> R.drawable.lock_outlined
        else -> R.drawable.in_progress
    }
}

enum class ServiceType(val title: String) {
    FULL_FREIGHT("Freight whole container"),
    AIR_FREIGHT("Freight air"),
    FORWARDING("Forwarding"),
    STORAGE("Storage"),
    PRR("PRR"),
    CUSTOMS("Customs"),
    TRANSPORT("Transport"),
    EUROPE_TRANSPORT("Auto"),
    UKRAINE_TRANSPORT("Auto"),
    OTHER("Other")
}