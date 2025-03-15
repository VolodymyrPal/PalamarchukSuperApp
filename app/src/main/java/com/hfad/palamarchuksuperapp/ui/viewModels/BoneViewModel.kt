package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.domain.models.AppError
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

class BoneViewModel @Inject constructor(


) : GenericViewModel<BusinessEntity, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    override val _dataFlow: Flow<Any> = emptyFlow()
    override val _errorFlow: Flow<AppError?> = emptyFlow()

    override fun event(event: ChatBotViewModel.Event) {

    }

    override val uiState: StateFlow<StateBone> = emptyFlow<StateBone>().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StateBone()
    )


    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    @Stable
    data class StateBone(
        val businessEntity: BusinessEntity = BusinessEntity(0, EntityDetails().toString()),
        val ordedList: PersistentList<Order> = persistentListOf(),
        val paymentList: PersistentList<Payment> = persistentListOf(),
    ) : State<BusinessEntity>
}

data class BusinessEntity(
    @PrimaryKey
    val code: Int = 1,
    val name: String = " Base card ",
    val entityType: EntityType = EntityType.OTHER,
    val manager: String = "",
)

enum class EntityType {
    HOLDING, RESIDENT, NONRESIDENT, FACTORY, OTHER
}

data class EntityDetails(
    val name: String = "",
)

data class Order(
    @PrimaryKey
    val id: Int,
    val businessEntityNum: Int,
    val num: Int,
    val serviceList: OrderService,
    val status: OrderStatus = OrderStatus.CREATED,
)

interface Stepper {
    val status: StepperStatus
    val serviceType: ServiceType

    @get:DrawableRes
    val icon: Int
}

enum class StepperStatus {
    DONE, CANCELED, IN_PROGRESS, CREATED
}

enum class OrderStatus {
    CREATED, CALCULATED, IN_PROGRESS, DONE
}

@Suppress("LongParameterList")
class OrderService(
    /* PrimaryKey - ID */
    val id: Int = 0,
    val orderId: Int? = null,
    override val serviceType: ServiceType = ServiceType.OTHER,
    val price: Float = 0.0f,
    val duration: Int = 0,
    override val status: StepperStatus = StepperStatus.CREATED,
    @DrawableRes override val icon: Int = R.drawable.lock_outlined,
) : Stepper

enum class ServiceType(val title: String) {
    FREIGHT("Freight"),
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
                ServiceType.FREIGHT,
                ServiceType.FORWARDING,
                ServiceType.TRANSPORT,
                ServiceType.CUSTOMS,
            )
        }
    }

    object ChinaEuropeContainer : ServiceScenario {
        override val scenario = listOf<ServiceType>(
            ServiceType.FREIGHT,
            ServiceType.FORWARDING,
            ServiceType.EUROPE_TRANSPORT,
        )
    }

    object ChinaUkraineContainer : ServiceScenario {
        override val scenario = listOf<ServiceType>(
            ServiceType.FREIGHT,
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


data class Payment(
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreatinon: Date,
)


val orderServiceList = persistentListOf<OrderService>(
    OrderService(
        id = 1,
        orderId = 101,
        price = 1500f,
        duration = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.FREIGHT,
    ),
    OrderService(
        id = 2,
        orderId = 102,
        price = 1200f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FORWARDING,
    ),
    OrderService(
        id = 3,
        orderId = 103,
        price = 500f,
        duration = 1,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.STORAGE,
    ),
    OrderService(
        id = 4,
        orderId = 104,
        price = 800f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    OrderService(
        id = 5,
        orderId = 105,
        price = 3000f,
        duration = 5,
        status = StepperStatus.DONE,
        serviceType = ServiceType.CUSTOMS,
    ),
    OrderService(
        id = 6,
        orderId = 106,
        price = 1800f,
        duration = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    OrderService(
        id = 7,
        orderId = 107,
        price = 2500f,
        duration = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    OrderService(
        id = 8,
        orderId = 108,
        price = 2200f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    OrderService(
        id = 9,
        orderId = 109,
        price = 600f,
        duration = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.OTHER,
    ),
    OrderService(
        id = 10,
        orderId = 110,
        price = 1700f,
        duration = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FREIGHT,
    ),
    OrderService(
        id = 11,
        orderId = 111,
        price = 1100f,
        duration = 2,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ),
    OrderService(
        id = 12,
        orderId = 112,
        price = 450f,
        duration = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    ),
    OrderService(
        id = 13,
        orderId = 113,
        price = 850f,
        duration = 2,
        status = StepperStatus.DONE,
        serviceType = ServiceType.PRR,
    ),
    OrderService(
        id = 14,
        orderId = 114,
        price = 3100f,
        duration = 5,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.CUSTOMS,
    ),
    OrderService(
        id = 15,
        orderId = 115,
        price = 1750f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.TRANSPORT,
    ),
    OrderService(
        id = 16,
        orderId = 116,
        price = 2550f,
        duration = 4,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    OrderService(
        id = 17,
        orderId = 117,
        price = 2300f,
        duration = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    OrderService(
        id = 18,
        orderId = 118,
        price = 700f,
        duration = 1,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    OrderService(
        id = 19,
        orderId = 119,
        price = 1600f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FREIGHT,
    ),
    OrderService(
        id = 20,
        orderId = 120,
        price = 1300f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    OrderService(
        id = 21,
        orderId = 121,
        price = 1350f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    OrderService(
        id = 22,
        orderId = 122,
        price = 1250f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    OrderService(
        id = 23,
        orderId = 123,
        price = 950f,
        duration = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.STORAGE,
    ),
    OrderService(
        id = 24,
        orderId = 124,
        price = 750f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.PRR,
    ),
    OrderService(
        id = 25,
        orderId = 125,
        price = 2800f,
        duration = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.CUSTOMS,
    ),
    OrderService(
        id = 26,
        orderId = 126,
        price = 1900f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.OTHER,
    ),
    OrderService(
        id = 27,
        orderId = 127,
        price = 2600f,
        duration = 4,
        status = StepperStatus.DONE,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    OrderService(
        id = 28,
        orderId = 128,
        price = 1000f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FREIGHT,
    ),
    OrderService(
        id = 29,
        orderId = 129,
        price = 1400f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ), OrderService(
        id = 30,
        orderId = 130,
        price = 400f,
        duration = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    )
)