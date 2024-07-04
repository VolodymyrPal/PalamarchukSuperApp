package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.toProductDomainRW
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    val repository: StoreRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {
    lateinit var testData: List<Product> //TODO

    init {
        viewModelScope.launch { testData = repository.fetchProductsTest().first() }
    }  //TODO

    sealed class Event : BaseEvent() {
        object FetchSkills : Event()
        object OnRefresh : Event()
        data class ShowToast(val message: String) : Event()
        data class AddProduct(val product: ProductDomainRW, val quantity: Int = 1) : Event()
        data class AddItemToBasket(val product: ProductDomainRW, val quantity: Int = 1) : Event()
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
                viewModelScope.launch {

                }
            }

            is Event.AddItemToBasket -> {
                viewModelScope.launch {
                    val newSkills = (uiState.first() as State.Success).data.toMutableList()
                    newSkills.indexOf(event.product).let {
                        newSkills[it] =
                            newSkills[it].copy(quantity = newSkills[it].quantity + event.quantity)
                        Log.d("TAG", "event: ${newSkills[it].quantity}")
                        emitState(newSkills)
                    }
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

