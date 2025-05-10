package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.State
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ClientEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.EntityDetails
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPayment
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceType
import com.hfad.palamarchuksuperapp.feature.bone.ui.composables.StepperStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BoneViewModel @Inject constructor(
) : GenericViewModel<ClientEntity, BoneViewModel.Event, BoneViewModel.Effect>() {

    override val _dataFlow: Flow<Any> = emptyFlow()
    override val _errorFlow: Flow<AppError?> = emptyFlow()

    override fun event(event: Event) {

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
        val clientEntity: ClientEntity = ClientEntity(0, EntityDetails().toString()),
        val orderList: List<Order> = listOf(),
        val cashPaymentList: List<CashPayment> = listOf(),
    ) : State<ClientEntity>
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
        duration = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 2,
        orderId = 102,
        price = 1200f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 3,
        orderId = 103,
        price = 500f,
        duration = 1,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 4,
        orderId = 104,
        price = 800f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 5,
        orderId = 105,
        price = 3000f,
        duration = 5,
        status = StepperStatus.DONE,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 6,
        orderId = 106,
        price = 1800f,
        duration = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 7,
        orderId = 107,
        price = 2500f,
        duration = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    ServiceOrder(
        id = 8,
        orderId = 108,
        price = 2200f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 9,
        orderId = 109,
        price = 600f,
        duration = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 10,
        orderId = 110,
        price = 1700f,
        duration = 3,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 11,
        orderId = 111,
        price = 1100f,
        duration = 2,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 12,
        orderId = 112,
        price = 450f,
        duration = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 13,
        orderId = 113,
        price = 850f,
        duration = 2,
        status = StepperStatus.DONE,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 14,
        orderId = 114,
        price = 3100f,
        duration = 5,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 15,
        orderId = 115,
        price = 1750f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.TRANSPORT,
    ),
    ServiceOrder(
        id = 16,
        orderId = 116,
        price = 2550f,
        duration = 4,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    ServiceOrder(
        id = 17,
        orderId = 117,
        price = 2300f,
        duration = 3,
        status = StepperStatus.DONE,
        serviceType = ServiceType.UKRAINE_TRANSPORT,
    ),
    ServiceOrder(
        id = 18,
        orderId = 118,
        price = 700f,
        duration = 1,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 19,
        orderId = 119,
        price = 1600f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 20,
        orderId = 120,
        price = 1300f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 21,
        orderId = 121,
        price = 1350f,
        duration = 2,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 22,
        orderId = 122,
        price = 1250f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.FORWARDING,
    ),
    ServiceOrder(
        id = 23,
        orderId = 123,
        price = 950f,
        duration = 1,
        status = StepperStatus.DONE,
        serviceType = ServiceType.STORAGE,
    ),
    ServiceOrder(
        id = 24,
        orderId = 124,
        price = 750f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.PRR,
    ),
    ServiceOrder(
        id = 25,
        orderId = 125,
        price = 2800f,
        duration = 4,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.CUSTOMS,
    ),
    ServiceOrder(
        id = 26,
        orderId = 126,
        price = 1900f,
        duration = 3,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.OTHER,
    ),
    ServiceOrder(
        id = 27,
        orderId = 127,
        price = 2600f,
        duration = 4,
        status = StepperStatus.DONE,
        serviceType = ServiceType.EUROPE_TRANSPORT,
    ),
    ServiceOrder(
        id = 28,
        orderId = 128,
        price = 1000f,
        duration = 2,
        status = StepperStatus.IN_PROGRESS,
        serviceType = ServiceType.FULL_FREIGHT,
    ),
    ServiceOrder(
        id = 29,
        orderId = 129,
        price = 1400f,
        duration = 3,
        status = StepperStatus.CREATED,
        serviceType = ServiceType.FORWARDING,
    ), ServiceOrder(
        id = 30,
        orderId = 130,
        price = 400f,
        duration = 1,
        status = StepperStatus.CANCELED,
        serviceType = ServiceType.STORAGE,
    )
)