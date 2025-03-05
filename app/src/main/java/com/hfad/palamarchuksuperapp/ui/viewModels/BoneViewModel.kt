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


) : GenericViewModel<HolderCard, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

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
        val holderCard: HolderCard = HolderCard(0, HolderInfo()),
        val ordedList: PersistentList<Order> = persistentListOf(),
        val paymentList: PersistentList<Payment> = persistentListOf(),
    ) : State<HolderCard>
}

data class HolderCard(
    @PrimaryKey
    val code: Int = 1,
    val name: String = " Base card ",
    val cartType: CardType = CardType.OTHER,
    val manager: String = "",
)

enum class CardType {
    HOLDER, RESIDENT, NONRESIDENT, FACTORY, OTHER
}

data class HolderInfo(
    val name: String = "",
)

data class Order(
    val odredNum: Int,
    val ordedClientName: String,
    val orderServices: OrderService,
)

data class OrderService(
    val serviceName: String,
    val servicePrice: Float,
)

data class Payment(
    val paymentNum: Int,
    val paymentSum: Float,
    val paymentDateCreatinon: Date,
)