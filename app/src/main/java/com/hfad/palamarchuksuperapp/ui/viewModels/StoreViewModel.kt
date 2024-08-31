package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.repository.FakeStoreApiRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository?,
    private val apiRepository: ProductRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    override suspend fun getData(): suspend () -> List<ProductDomainRW> {
        return apiRepository::getProductsDomainRw
    }
    private val dataFlow = repository!!.fetchProductsAsFlowFromDB

    val myFlow = combine(
        dataFlow, uiState
    ) { flow, state ->
        Log.d("TAG", "myFlow: $state")
        emitState(flow)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = State.Empty(loading = true)
    ).also {
        viewModelScope.launch {
            repository!!.upsertAll(apiRepository.getProductsDomainRw())
        }
    }

    override suspend fun getDataFlow(): Flow<List<ProductDomainRW>> = repository!!.fetchProductsAsFlowFromDB

    val baskList = uiState.map { state ->
        when (state) {
            is State.Success -> state.items.filter { it.quantity > 0 }
            else -> emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _basketList = MutableStateFlow<List<ProductDomainRW>>(emptyList())
    val basketList = _basketList.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.Eagerly
    )

    sealed class Event : BaseEvent {
        object FetchSkills : Event()
        object OnRefresh : Event()
        data class ShowToast(val message: String) : Event()
        data class AddProduct(val productId: Int, val quantity: Int = 1) : Event()
        data class SetItemToBasket(val productId: Int, val quantity: Int = 1) : Event()
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
                viewModelScope.launch { emitRefresh() }
            }

            is Event.OnRefresh -> {
                viewModelScope.launch { emitRefresh() }
            }

            is Event.ShowToast -> {
                viewModelScope.launch { effect(Effect.ShowToastAndVibration(event.message)) }
            }

            is Event.AddProduct -> {
                addProduct(event.productId, event.quantity)
            }

            is Event.SetItemToBasket -> {
                setItemToBasket(event.productId, event.quantity)
            }
        }
    }

    private fun updateBasketList(state: State.Success<List<ProductDomainRW>>) {
        val updatedBasketList = state.items.filter { it.quantity > 0 }
        _basketList.update { updatedBasketList }
    }


    private fun setItemToBasket(productId: Int, quantity: Int = 1) {
        try {
            viewModelScope.launch {
                val newSkills =
                    ((uiState.first() as State.Success).items!!.toMutableList()) //uiState.first() as State.Success).items.toMutableList()
                val foundProduct = newSkills.find { it.product.id == productId }
                newSkills.indexOf(foundProduct).let {
                    newSkills[it] = newSkills[it].copy(quantity = quantity)
                    emitState(newSkills)
                }
            }
        } catch (e: Exception) {
            Log.d("Exception in SetItemToBasket", "event: ${e.message}")
        }
    }

    private fun addProduct(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            val newSkills =
                (uiState.first() as State.Success).items.toMutableList()  //uiState.first() as State.Success).items.toMutableList()
            val skill = newSkills.find { it.product.id == productId }
            newSkills.indexOf(skill).let {
                var newQuantity = 0
                if (newSkills[it].quantity + skill?.quantity!! >= 0) {
                    newQuantity = newSkills[it].quantity + quantity
                }
                newSkills[it] =
                    newSkills[it].copy(quantity = newQuantity)
                emitState(newSkills)
                //(data as State.Success<*>).items = newSkills
            }
        }
    }
}