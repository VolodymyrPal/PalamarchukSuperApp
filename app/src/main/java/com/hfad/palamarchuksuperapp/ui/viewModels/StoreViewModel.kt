package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.repository.FakeStoreApiRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val apiRepository: FakeStoreApiRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    override val _dataFlow = repository.fetchProductsAsFlowFromDB

    override val uiState: StateFlow<State<List<ProductDomainRW>>>
        get() = combine(_dataFlow, _errorFlow, _loading) { data, error, loading ->
            State(
                items = data,
                error = error,
                loading = loading
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = State(loading = true)
            )
            .also {
                viewModelScope.launch {
                    repository.upsertAll(apiRepository.getProductsDomainRw())
                    _loading.value = false
                }
            }


    val baskList = uiState.map { state ->
        if (!state.items.isNullOrEmpty()) {
            state.items.filter {
                it.quantity > 0
            }
        } else {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    sealed class Event : BaseEvent {
        object FetchSkills : Event()
        object OnSoftRefresh : Event()
        object OnHardRefresh : Event()
        data class ShowToast(val message: String) : Event()
        data class AddProduct(val productDomainRW: ProductDomainRW, val quantity: Int = 1) : Event()
        data class SetItemToBasket(val productDomainRW: ProductDomainRW, val quantity: Int = 1) :
            Event()
    }

    sealed class Effect : BaseEffect {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
        data class ShowToastAndVibration(val message: String) : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.FetchSkills -> {
                viewModelScope.launch { repository.softRefreshProducts() }
            }

            is Event.OnSoftRefresh -> {
                viewModelScope.launch { repository.softRefreshProducts() }
            }

            is Event.OnHardRefresh -> {
                viewModelScope.launch { repository.hardRefreshProducts() }
            }

            is Event.ShowToast -> {
                viewModelScope.launch { effect(Effect.ShowToastAndVibration(event.message)) }
            }

            is Event.AddProduct -> {
                addProduct(event.productDomainRW, event.quantity)
            }

            is Event.SetItemToBasket -> {
                setItemToBasket(event.productDomainRW, event.quantity)
            }
        }
    }

    private fun setItemToBasket(productDomainRW: ProductDomainRW, quantity: Int = 1) {
        viewModelScope.launch {
            repository.updateProduct(productDomainRW.copy(quantity = quantity))
        }
    }

    private fun addProduct(productDomainRW: ProductDomainRW, quantity: Int = 1) {
        viewModelScope.launch {
            val newQuantity = productDomainRW.quantity + quantity
            repository.updateProduct(productDomainRW.copy(quantity = if (newQuantity < 0) 0 else newQuantity))
        }
    }
}