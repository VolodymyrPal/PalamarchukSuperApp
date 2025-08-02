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

sealed interface ServiceScenario {
    val scenario: List<ServiceType>

    sealed interface NonEuropeContainer : ServiceScenario {
        object WithFreight : NonEuropeContainer {
            override val scenario = listOf<ServiceType>(
                ServiceType.FULL_FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.TRANSPORT,
                ServiceType.CUSTOMS,
            )
        }
    }

    object ChinaEuropeContainer : ServiceScenario {
        override val scenario = listOf<ServiceType>(
            ServiceType.FULL_FREIGHT,
            ServiceType.FORWARDING,
            ServiceType.EUROPE_TRANSPORT,
        )
    }

    object ChinaUkraineContainer : ServiceScenario {
        override val scenario = listOf<ServiceType>(
            ServiceType.FULL_FREIGHT,
            ServiceType.FORWARDING,
            ServiceType.UKRAINE_TRANSPORT,
        )
    }

    object SimpleEurope : ServiceScenario {
        override val scenario = listOf<ServiceType>(
            ServiceType.TRANSPORT,
            ServiceType.CUSTOMS,
        )
    }

    data class DynamicScenario(
        override val scenario: List<ServiceType>,
    ) : ServiceScenario
}


val serviceOrderLists = listOf(
    ServiceOrder(
        id = 1,
        orderId = 101,
        price = 1500f,
        durationDay = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 2,
        orderId = 102,
        price = 1200f,
        durationDay = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 3,
        orderId = 103,
        price = 500f,
        durationDay = 1,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 4,
        orderId = 104,
        price = 800f,
        durationDay = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 5,
        orderId = 105,
        price = 3000f,
        durationDay = 5,
        status = StepperStatus.DONE,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 6,
        orderId = 106,
        price = 1800f,
        durationDay = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 7,
        orderId = 107,
        price = 2500f,
        durationDay = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    ServiceOrder(
        id = 8,
        orderId = 108,
        price = 2200f,
        durationDay = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 9,
        orderId = 109,
        price = 600f,
        durationDay = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 10,
        orderId = 110,
        price = 1700f,
        durationDay = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 11,
        orderId = 111,
        price = 1100f,
        durationDay = 2,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 12,
        orderId = 112,
        price = 450f,
        durationDay = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 13,
        orderId = 113,
        price = 850f,
        durationDay = 2,
        status = StepperStatus.DONE,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 14,
        orderId = 114,
        price = 3100f,
        durationDay = 5,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 15,
        orderId = 115,
        price = 1750f,
        durationDay = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.TRANSPORT,
    ),
    ServiceOrder(
        id = 16,
        orderId = 116,
        price = 2550f,
        durationDay = 4,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    ServiceOrder(
        id = 17,
        orderId = 117,
        price = 2300f,
        durationDay = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    ServiceOrder(
        id = 18,
        orderId = 118,
        price = 700f,
        durationDay = 1,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 19,
        orderId = 119,
        price = 1600f,
        durationDay = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 20,
        orderId = 120,
        price = 1300f,
        durationDay = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 21,
        orderId = 121,
        price = 1350f,
        durationDay = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 22,
        orderId = 122,
        price = 1250f,
        durationDay = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 23,
        orderId = 123,
        price = 950f,
        durationDay = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 24,
        orderId = 124,
        price = 750f,
        durationDay = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 25,
        orderId = 125,
        price = 2800f,
        durationDay = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 26,
        orderId = 126,
        price = 1900f,
        durationDay = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 27,
        orderId = 127,
        price = 2600f,
        durationDay = 4,
        status = StepperStatus.DONE,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    ServiceOrder(
        id = 28,
        orderId = 128,
        price = 1000f,
        durationDay = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 29,
        orderId = 129,
        price = 1400f,
        durationDay = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ), ServiceOrder(
        id = 30,
        orderId = 130,
        price = 400f,
        durationDay = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    )
)