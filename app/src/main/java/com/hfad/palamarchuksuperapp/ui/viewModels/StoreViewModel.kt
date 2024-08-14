package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.data.repository.ProductRepository
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.common.toProductDomainRW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
class StoreViewModel @Inject constructor(
    val repository: StoreRepository,
    val apiRepository: ProductRepository,
) : GenericViewModel<List<ProductDomainRW>, StoreViewModel.Event, StoreViewModel.Effect>() {

    lateinit var testData: List<ProductDomainRW>

    init {
        event(Event.FetchSkills)
    }

    private fun getProductsAsFlow(): Flow<List<Product>> = flow {
        val products = apiRepository.fetchProducts()
        emit(products)
    }.flowOn(Dispatchers.IO)

    private var _products: Flow<Async<List<ProductDomainRW>>> =
        combine(
            getProductsAsFlow(), _isLoading
        ) { products, _ ->
            products.map { it.toProductDomainRW() }
        }
            .map { Async.Success(it) }
            .catch { Async.Error(it) }

    val myState: StateFlow<MyState> = combine(
        _isLoading, _products
    ) { isLoading, products ->
        when (products) {
            is Async.Loading -> {
                MyState(isLoading)
            }

            is Async.Error -> {
                MyState(
                    loading = isLoading,  //TODO for testing (in product must be - false)
                    message = products.errorMessage.message!!)
            }

            is Async.Success -> {
                MyState(
                    loading = isLoading, //TODO for testing (in product must be - false)
                    items = products.data,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MyState(loading = true)
    )

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
                viewModelScope.launch {
                    emitState(apiRepository.fetchProducts().map { it.toProductDomainRW() })
                }
            }


            is Event.OnRefresh -> {
                refreshProducts()
            //refresh()
            }

            is Event.ShowToast -> {

            }

            is Event.AddProduct -> {
                //addProduct(event.product, event.quantity)
                //refresh()
            }

            is Event.SetItemToBasket -> {
                //setItemToBasket(event.product, event.quantity)
                //refresh()
            }
        }
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                apiRepository.fetchProducts()
            } catch (e: Exception) {
                Log.d("Exception in refresh", "event: ${e.message}")
            }
            delay(1500)
            _isLoading.value = false
        }
    }


    private fun refreshProducts() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val newProducts = apiRepository.fetchProducts()

                _products = combine(
                    flowOf(newProducts), _isLoading
                ) { products, _ ->
                    products.map { it.toProductDomainRW() }
                }
                    .map { Async.Success(it) }
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = Async.Loading
                    )
            } catch (e: Exception) {
                _products = flowOf(Async.Error(e))
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = Async.Error(e)
                    )
            } finally {
                delay(1500)
                _isLoading.value = false
            }
        }
    }


    private fun setItemToBasket(product: ProductDomainRW, quantity: Int = 1) {
        try {
            viewModelScope.launch {
                val newSkills = (uiState.first() as State.Success).data.toMutableList()
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
            val newSkills = (uiState.first() as State.Success).data.toMutableList()
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
}
//    private fun fetchProducts() {
//        emitState(emitProcessing = true) {
//            try {
//                val products = withContext(Dispatchers.IO) {
//                    apiRepository.fetchProducts()
//                }
//                val skills = products.map { it.toProductDomainRW() }
//                Log.d("VM emitState data: ", "$skills")
//                return@emitState if (skills.isNotEmpty()) State.Success(data = skills) else State.Empty
//
//            } catch (e: Exception) {
//                Log.d("Eror in fetchProducts", "event: ${e.message}")
//                State.Error(e)
//            }
//        }
//    }
