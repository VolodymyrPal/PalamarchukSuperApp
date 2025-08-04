package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ClientEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.EntityDetails
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BoneViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : GenericViewModel<BoneViewModel.StateBone, BoneViewModel.Event, BoneViewModel.Effect>() {

    val pagingDataFlow: Flow<PagingData<Order>> =
        ordersRepository.pagingOrders(null).cachedIn(viewModelScope)

    override val _dataFlow: Flow<Any> = emptyFlow()
    override val _errorFlow: Flow<AppError?> = emptyFlow()

    override fun event(event: Event) {
        when (event) {
            is Event.Init -> {
            }

            is Event.SendImage -> {}
        }
    }

    override val uiState: StateFlow<StateBone> = emptyFlow<StateBone>().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StateBone()
    )


    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
        object Init : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    @Stable
    data class StateBone(
        val clientEntity: ClientEntity = ClientEntity(0, EntityDetails().toString()),
    ) : ScreenState
}