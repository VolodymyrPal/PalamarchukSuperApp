package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch

@Stable
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : GenericViewModel<PersistentList<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    data class StoreState(
        val items: PersistentList<ProductDomainRW> = persistentListOf(),
        val loading: Boolean = false,
        val error: AppError? = null,
    ) : State<PersistentList<ProductDomainRW>>

    override val _dataFlow: Flow<Result<PersistentList<ProductDomainRW>, AppError>> =
        repository.fetchProductsAsFlowFromDB
            .map<PersistentList<ProductDomainRW>, Result<PersistentList<ProductDomainRW>, AppError>> {
                Result.Success(it)
            }
            .catch {
                effect(Effect.ShowToast(it.message ?: "Error. Please provide info to developer"))
            }

    override val _errorFlow: MutableStateFlow<AppError?> = repository.errorFlow

    override val uiState: StateFlow<StoreState> =
        combine(_dataFlow, _loading) { data, loading ->
            when (data) {
                is Result.Success -> {
                    StoreState(
                        items = data.data,
                        loading = loading
                    )
                }

                is Result.Error -> {
                    StoreState(
                        error = data.error,
                        loading = loading
                    )
                }
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StoreState(loading = true)
            )
            .also {
                viewModelScope.launch(ioDispatcher) {
                    event(Event.OnSoftRefresh)
                }
            }


    val baskList = uiState.map { state ->
        if (state.items.isNotEmpty()) {
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
                viewModelScope.launch (ioDispatcher) {
                    repository.softRefreshProducts()
                }
            }

            is Event.OnSoftRefresh -> {
                _loading.update { true }
                viewModelScope.launch (ioDispatcher) {
                    delay(750)
                    repository.softRefreshProducts()
                    _loading.update { false }
                }
            }

            is Event.OnHardRefresh -> {
                _loading.update { true }
                viewModelScope.launch (ioDispatcher) {
                    delay(1500)
                    repository.hardRefreshProducts()
                    _loading.update { false }
                }
            }

            is Event.ShowToast -> {
                viewModelScope.launch (mainDispatcher) { effect(Effect.ShowToastAndVibration(event.message)) }
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
        viewModelScope.launch (mainDispatcher) {
            repository.updateProduct(productDomainRW.copy(quantity = quantity))
        }
    }

    private fun addProduct(productDomainRW: ProductDomainRW, quantity: Int = 1) {
        viewModelScope.launch (mainDispatcher) {
            val newQuantity = productDomainRW.quantity + quantity
            repository.updateProduct(productDomainRW.copy(quantity = if (newQuantity < 0) 0 else newQuantity))
        }
    }
}