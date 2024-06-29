package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.lifecycle.viewModelScope
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

    sealed class Event : BaseEvent() {
        object FetchSkills : Event()
        object OnRefresh : Event()
        data class ShowToast(val message: String) : Event()
        data class AddProduct(val product: ProductDomainRW, val quantity: Int = 1) : Event()
    }

    sealed class Effect: BaseEffect() {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.FetchSkills -> {
                emitState(emitProcessing = true) { current ->
                    emitState(State.Processing)
                    if (current is State.Error) {
                        return@emitState current
                    }
                    try {
                        val skills = repository.fetchProducts()
                        return@emitState if (skills.first().isNotEmpty()) {
                            State.Success(data = skills)
                        } else State.Empty
                    } catch (e: Exception) {
                        State.Error(e)
                    }
                }
            }


            is Event.OnRefresh -> {

            }

            is Event.ShowToast -> {

            }
            is Event.AddProduct -> {
                viewModelScope.launch {

                }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {

        }
    }
}

