package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.generateOrderItems
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.random.Random

class OrderPageViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : GenericViewModel<OrderPageState, OrderPageViewModel.OrderPageEvent, OrderPageViewModel.OrderPageEffect>() {

    override val _dataFlow: StateFlow<List<Order>> = flow {
        emit(generateOrderItems())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val statisticFlow: StateFlow<OrderStatistics> = flow {
        emit(generateOrderStatistic())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        OrderStatistics() // TODO for testing
    )

    override val uiState: StateFlow<OrderPageState> =
        _dataFlow.combine(statisticFlow) { orders, orderMetrics ->
            OrderPageState(
                orders = orders,
                orderMetrics = orderMetrics
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OrderPageState()
        )


    override fun event(event: OrderPageEvent) {

    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)


    sealed class OrderPageEvent : BaseEvent {
        data class LoadOrders(val clientId: Int) : OrderPageEvent()
        data class RefreshOrders(val clientId: Int) : OrderPageEvent()
    }

    sealed class OrderPageEffect : BaseEffect {
        data class ShowOrder(val orderId: Int) : OrderPageEffect()
    }

}


internal fun generateOrderStatistic(): OrderStatistics {
    return OrderStatistics(
        totalOrders = Random.nextInt(20, 30),
        completedOrders = Random.nextInt(5, 15),
        inProgressOrders = Random.nextInt(1, 5),
        totalOrderWeight = Random.nextInt(10, 20) + (Random.nextFloat() * 100).toInt() / 100f
    )
}