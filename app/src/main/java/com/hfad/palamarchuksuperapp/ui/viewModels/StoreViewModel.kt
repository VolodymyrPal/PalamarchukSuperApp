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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private val dataLoader: DataLoader<List<ProductDomainRW>> = DataLoader()
    private val refreshTrigger = RefreshTrigger()

    val data = dataLoader.loadAndObserveRefreshData(
        coroutineScope = viewModelScope,
        refreshTrigger = refreshTrigger,
        initialData = State.Processing,
        fetchData = { fetchProducts(it) },
        onErrorAction = {},
        onRefreshDone = {}
    )

    val baskList = data.map { state ->
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
                    refreshTrigger.refresh()
                }
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    refreshTrigger.refresh()
                }
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
                val newSkills = (data.first() as State.Success).items.toMutableList()
                val foundProduct = newSkills.find { it.product.id == product.product.id }
                newSkills.indexOf(foundProduct).let {
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
            val newSkills = (data.first() as State.Success).items.toMutableList()
            val skill = newSkills.find { it.product.id == product.product.id }
            newSkills.indexOf(skill).let {
                var newQuantity = 0
                if (newSkills[it].quantity + product.quantity >= 0) {
                    newQuantity = newSkills[it].quantity + quantity
                }
                newSkills[it] =
                    newSkills[it].copy(quantity = newQuantity)
                (data as State.Success<*>).items = newSkills
            }
        }
    }

    private suspend fun fetchProducts(state: State<*>): Result<List<ProductDomainRW>> {
        try {
            val products = withContext(Dispatchers.IO) {
                apiRepository.fetchProducts()
            }
            val skills = products.map { it.toProductDomainRW() }
            delay(2000)
            if (Random.nextFloat() > 0.5) throw Exception("Something went wrong") //TODO
            return Result.success(skills)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}