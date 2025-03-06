package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
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

data class OrderService(
    @PrimaryKey
    val id: Int,
    val orderId: Int?,
    val name: ServiceType,
    val price: Float,
    val duration: Int,
)

data class Payment(
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreatinon: Date,
)