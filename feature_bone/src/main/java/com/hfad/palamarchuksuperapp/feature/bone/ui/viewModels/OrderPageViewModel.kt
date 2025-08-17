package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.UserSession
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

class OrderPageViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
    userRepository: AuthRepository,
) : GenericViewModel<OrderPageState, OrderPageViewModel.OrderPageEvent, OrderPageViewModel.OrderPageEffect>() {

    override val _dataFlow: Flow<UserSession> = userRepository.currentSession

    private val orderStatusFilter: MutableStateFlow<OrderStatus?> = MutableStateFlow(null)
    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    val orderPaging: Flow<PagingData<Order>> =
        combine(orderStatusFilter, searchQuery) { status, query -> status to query }
            .distinctUntilChanged()
            .debounce(500)
            .flatMapLatest { (status, query) ->
                ordersRepository.pagingOrders(status, query).cachedIn(viewModelScope)
            }

    private val statisticFlow: Flow<AppResult<OrderStatistics, AppError>> =
        ordersRepository.orderStatistics

    override val uiState: StateFlow<OrderPageState> =
        combine(
            _dataFlow, statisticFlow, orderStatusFilter, searchQuery
        ) { userSession, orderMetrics, status, query ->
            val orderMetrics = if (orderMetrics is AppResult.Success) {
                orderMetrics.data
            } else {
                _errorFlow.emit((orderMetrics as AppResult.Error).error)
                OrderStatistics()
            }
            OrderPageState(
                orderMetrics = orderMetrics,
                orderStatusFilter = status,
                searchQuery = query
            )
        }.onStart {
            ordersRepository.refreshStatistic()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OrderPageState()
        )


    override fun event(event: OrderPageEvent) {
        when (event) {
            is OrderPageEvent.LoadOrders -> {

            }

            is OrderPageEvent.RefreshOrders -> {

            }

            is OrderPageEvent.FilterOrderStatus -> {
                orderStatusFilter.update { event.status }
            }

            is OrderPageEvent.Search -> {
                Log.d("OrderPageViewModel", "Search query: ${event.query}")
                searchQuery.update { event.query }
            }
        }
    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)


    sealed class OrderPageEvent : BaseEvent {
        data class LoadOrders(val clientId: Int) : OrderPageEvent()
        data class RefreshOrders(val clientId: Int) : OrderPageEvent()
        data class FilterOrderStatus(val status: OrderStatus?) : OrderPageEvent()
        data class Search(val query: String) : OrderPageEvent()
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