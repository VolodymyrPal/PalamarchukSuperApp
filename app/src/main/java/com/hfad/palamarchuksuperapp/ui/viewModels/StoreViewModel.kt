package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.repository.ProductRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.common.toProductDomainRW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Stable
class StoreViewModel @Inject constructor(
    val repository: StoreRepository,
    val apiRepository: ProductRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    lateinit var testData: List<ProductDomainRW>

    init {
        event(Event.FetchSkills)

        viewModelScope.launch {
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
            launch {
                testData = repository.fetchProductsTest().first().map { it.toProductDomainRW() }
            }
        }

    }

    private fun updateBasketList(state: State.Success<List<ProductDomainRW>>) {
        val updatedBasketList = state.data.filter { it.quantity > 0 }
        _basketList.update { updatedBasketList }
    }

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
        data class AddProduct(val product: ProductDomainRW, val quantity: Int = 1) : Event()
        data class SetItemToBasket(val product: ProductDomainRW, val quantity: Int = 1) : Event()
    }

    sealed class Effect : BaseEffect {
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
                    val newSkills = (uiState.first() as State.Success).data.toMutableList()
                    val skill = newSkills.find { it.product.id == event.product.product.id }
                    newSkills.indexOf(skill).let {
                        var newQuantity = 0
                        if (newSkills[it].quantity + event.quantity >= 0) {
                            newQuantity = newSkills[it].quantity + event.quantity
                        }
                        newSkills[it] =
                            newSkills[it].copy(quantity = newQuantity)
                        emitState(newSkills)
                    }
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
        emitState(emitProcessing = true) { current ->
            try {
                val products = withContext(Dispatchers.IO) {
                    apiRepository.fetchProducts()
                }
                val skills = products.map { it.toProductDomainRW() }
                Log.d("VM emitState data: ", "$skills")
                return@emitState if (skills.isNotEmpty()) State.Success(data = skills) else State.Empty

            } catch (e: Exception) {
                Log.d("Eror in fetchProducts", "event: ${e.message}")
                State.Error(e)
            }
        }
    }
}

