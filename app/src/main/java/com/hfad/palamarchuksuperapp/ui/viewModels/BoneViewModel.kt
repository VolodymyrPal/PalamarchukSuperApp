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