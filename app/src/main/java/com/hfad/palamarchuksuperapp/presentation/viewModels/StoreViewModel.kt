package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.toProductDomainRW
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    val repository: StoreRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {
    lateinit var testData: List<Product> //TODO

    init {
        viewModelScope.launch {
            launch {
                testData = repository.fetchProductsTest().first()               //TODO
            }

            launch {
                uiState.collect { state ->
                    when (state) {
                        is State.Success -> {
                            updateBasketList(state)
                        }

                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }

    private fun updateBasketList(state: State.Success<List<ProductDomainRW>>) {
        val updatedBasketList = state.data.filter { it.quantity > 0 }
        _basketList.value = updatedBasketList
    }

    private val _basketList = MutableStateFlow<List<ProductDomainRW>>(emptyList())
    val basketList = _basketList.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    sealed class Event : BaseEvent() {
        object FetchSkills : Event()
        object OnRefresh : Event()
        data class ShowToast(val message: String) : Event()
        data class AddProduct(val product: ProductDomainRW, val quantity: Int = 1) : Event()
        data class SetItemToBasket(val product: ProductDomainRW, val quantity: Int = 1) : Event()
    }

    sealed class Effect : BaseEffect() {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.FetchSkills -> {
                fetchProducts()
            }


            is Event.OnRefresh -> {
                fetchProducts()
            }

            is Event.ShowToast -> {

            }

            is Event.AddProduct -> {
                try {
                    viewModelScope.launch {
                        val newSkills = (uiState.first() as State.Success).data.toMutableList()
                        val product = newSkills.find { it.product.id == event.product.product.id }
                        Log.d("Product quantity: ", "event: ${product?.quantity}")
                        newSkills.indexOf(product).let {
                            newSkills[it] = newSkills[it].copy(
                                quantity = if (it>0) {it+event.quantity} else 0
                            )
                            emitState(newSkills)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Exception in SetItemToBasket", "event: ${e.message}")
                }
            }

            is Event.SetItemToBasket -> {
                try {
                    viewModelScope.launch {
                        val newSkills = (uiState.first() as State.Success).data.toMutableList()
                        val product = newSkills.find { it.product.id == event.product.product.id }
                        newSkills.indexOf(product).let {
                            newSkills[it] = newSkills[it].copy(quantity = event.quantity)
                            emitState(newSkills)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Exception in SetItemToBasket", "event: ${e.message}")
                }
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            emitState(emitProcessing = true) { current ->
                emitState(State.Processing)
                if (current is State.Error) {
                    return@emitState current
                }
                try {
                    val skills = repository.fetchProducts().map { products ->
                        products.map { it.toProductDomainRW() }
                    }

                    return@emitState if (skills.first().isNotEmpty()) {
                        State.Success(data = skills.first())
                    } else State.Empty
                } catch (e: Exception) {
                    State.Error(e)
                }
            }
        }
    }
}

