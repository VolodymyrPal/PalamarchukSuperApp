package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.State
import com.hfad.palamarchuksuperapp.domain.models.Product
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : GenericViewModel<PersistentList<Product>, StoreViewModel.Event, StoreViewModel.Effect>() {

    data class StoreState(
        val items: PersistentList<Product> = persistentListOf(),
        val loading: Boolean = false,
        val error: AppError? = null,
    ) : State<PersistentList<Product>>

    private val productsFlow: Flow<PersistentList<Product>> = flow {
        emitAll(repository.fetchProductsAsFlowFromDB)
    }.onStart {
        repository.softRefreshProducts()
    }.distinctUntilChanged()


//    override val _dataFlow: Flow<Result<PersistentList<Product>, AppError>> = productsFlow.map {
//        Result.Success(it)
//    }
    override val _dataFlow: Flow<Result<PersistentList<Product>, AppError>> =
        repository.fetchProductsAsFlowFromDB
            .map<PersistentList<Product>, Result<PersistentList<Product>, AppError>> {
                Result.Success(it)
            }
            .catch {
                effect(Effect.ShowToast(it.message ?: "Error. Please provide info to developer"))
            }
            .onStart {
                viewModelScope.launch(ioDispatcher) {
                    event(Event.OnSoftRefresh)
                }
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
        data class AddProduct(val product: Product, val quantity: Int = 1) : Event()
        data class SetItemToBasket(val product: Product, val quantity: Int = 1) :
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
                viewModelScope.launch(ioDispatcher) {
                    repository.softRefreshProducts()
                }
            }

            is Event.OnSoftRefresh -> {
                _loading.update { true }
                viewModelScope.launch(ioDispatcher) {
                    delay(750)
                    repository.softRefreshProducts()
                    _loading.update { false }
                }
            }

            is Event.OnHardRefresh -> {
                _loading.update { true }
                viewModelScope.launch(ioDispatcher) {
                    delay(1500)
                    repository.hardRefreshProducts()
                    _loading.update { false }
                }
            }

            is Event.ShowToast -> {
                viewModelScope.launch(mainDispatcher) { effect(Effect.ShowToastAndVibration(event.message)) }
            }

            is Event.AddProduct -> {
                addProduct(event.product, event.quantity)
            }

            is Event.SetItemToBasket -> {
                setItemToBasket(event.product, event.quantity)
            }
        }
    }

    private fun setItemToBasket(product: Product, quantity: Int = 1) {
        viewModelScope.launch(mainDispatcher) {
            repository.updateProduct(product.copy(quantity = quantity))
        }
    }

    private fun addProduct(product: Product, quantity: Int = 1) {
        viewModelScope.launch(mainDispatcher) {
            val newQuantity = product.quantity + quantity
            repository.updateProduct(product.copy(quantity = if (newQuantity < 0) 0 else newQuantity))
        }
    }
}