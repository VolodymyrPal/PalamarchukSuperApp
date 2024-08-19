package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.repository.ProductRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.common.toProductDomainRW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@Stable
class StoreViewModel @Inject constructor(
    val repository: StoreRepository,
    val apiRepository: ProductRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    val refreshTrigger = RefreshTrigger()



    private suspend fun fetchProduct(state : State<List<ProductDomainRW>>): Result<List<ProductDomainRW>> {
        val products = withContext(Dispatchers.IO) {
            apiRepository.fetchProducts()
        }
        val skills = products.map { it.toProductDomainRW() }
        return if (skills.isNotEmpty()) Result.success(skills) else Result.failure(Exception("Something went wrong"))
    }


    init {
        event(Event.FetchSkills)
        viewModelScope.launch {
            uiState.collectLatest {
                if (it is State.Success) {
                    updateBasketList(it)
                }
            }
        }
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
                viewModelScope.launch {
                    emitState(apiRepository.fetchProducts().map { it.toProductDomainRW() })
                }
            }

            is Event.OnRefresh -> {
                fetchProducts()
            }

            is Event.ShowToast -> {

            }

            is Event.AddProduct -> {
                addProduct(event.product, event.quantity)
            }

            is Event.SetItemToBasket -> {
                setItemToBasket(event.product, event.quantity)
            }
        }
    }

    private fun updateBasketList(state: State.Success<List<ProductDomainRW>>) {
        val updatedBasketList = state.items.filter { it.quantity > 0 }
        _basketList.update { updatedBasketList }
    }


    private fun setItemToBasket(product: ProductDomainRW, quantity: Int = 1) {
        try {
            viewModelScope.launch {
                val newSkills = (uiState.first() as State.Success).items.toMutableList()
                val product = newSkills.find { it.product.id == product.product.id }
                newSkills.indexOf(product).let {
                    newSkills[it] = newSkills[it].copy(quantity = quantity)
                    emitState(newSkills)
                }
            }
        } catch (e: Exception) {
            Log.d("Exception in SetItemToBasket", "event: ${e.message}")
        }
    }

    private fun addProduct(product: ProductDomainRW, quantity: Int = 1) {
        viewModelScope.launch {
            val newSkills = (uiState.first() as State.Success).items.toMutableList()
            val skill = newSkills.find { it.product.id == product.product.id }
            newSkills.indexOf(skill).let {
                var newQuantity = 0
                if (newSkills[it].quantity + product.quantity >= 0) {
                    newQuantity = newSkills[it].quantity + quantity
                }
                newSkills[it] =
                    newSkills[it].copy(quantity = newQuantity)
                emitState(newSkills)
            }
        }
    }

    private fun fetchProducts() {
        emitState(emitProcessing = true) {
            try {
                val products = withContext(Dispatchers.IO) {
                    apiRepository.fetchProducts()
                }
                val skills = products.map { it.toProductDomainRW() }
                delay(2000)
                if (Random.nextFloat() > 0.5) throw Exception("Something went wrong") //TODO
                return@emitState if (skills.isNotEmpty()) State.Success(items = skills) else State.Empty(
                    loading = false
                )
            } catch (e: Exception) {
                State.Error(e)
            }
        }
    }

//    private fun getProductsAsFlow(): Flow<List<Product>> = flow {
//        val products = apiRepository.fetchProducts()
//        emit(products)
//    }.flowOn(Dispatchers.IO)
//


}