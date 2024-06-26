package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.StoreRepository
import com.hfad.palamarchuksuperapp.presentation.common.ProductToProductDomainRW
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreViewModel @Inject constructor(private val repository: StoreRepository,
) : ViewModel(), StoreContract {

    private val mutableState = MutableStateFlow(StoreContract.State.Empty)
    override val state: StateFlow<StoreContract.State<StoreContract.State.Empty>> =
        mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<StoreContract.Effect>()
    override val effect: SharedFlow<StoreContract.Effect> = effectFlow.asSharedFlow()

    override fun event(event: StoreContract.Event) {
        when (event) {
            is StoreContract.Event.OnGetNewsList -> {

            }
            is StoreContract.Event.OnSetShowFavoriteList -> {

            }
            is StoreContract.Event.OnRefresh -> {

            }
            is StoreContract.Event.OnBackPressed -> {

            }
            is StoreContract.Event.ShowToast -> {

            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val a = repository.fetchProducts().first().map { ProductToProductDomainRW.map(it) }
        }
    }
}

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}

interface StoreContract :
    UnidirectionalViewModel<StoreContract.State<*>, StoreContract.Event, StoreContract.Effect> {

    sealed interface State<out T> {
        data object Processing : State<Nothing>

        data class Success<out T>(val data: T, ) : State<T>

        data object Empty : State<Nothing>

        data class Error(val exception: Throwable) : State<Nothing>
    }

    sealed class Event {
        data class OnGetNewsList(val showFavoriteList: Boolean) : Event()
        data class OnSetShowFavoriteList(val showFavoriteList: Boolean) : Event()
        object OnRefresh : Event()
        object OnBackPressed : Event()
        data class ShowToast(val message: String) : Event()
    }

    sealed class Effect {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
    }
}

abstract class UIStateViewModel<T> : ViewModel(), StoreContract {

    private val _uiState: MutableStateFlow<RepoResult<T>> = MutableStateFlow(RepoResult.Empty)

    val uiState: StateFlow<RepoResult<T>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RepoResult.Empty
        )


    private val mutableState = MutableStateFlow(StoreContract.State.Empty)
    override val state: StateFlow<StoreContract.State.Empty> =
        mutableState.asStateFlow()

    private val effectFlow = MutableSharedFlow<StoreContract.Effect>()
    override val effect: SharedFlow<StoreContract.Effect> =
        effectFlow.asSharedFlow()

    protected fun emitState(
        emitProcessing: Boolean,
        block: suspend (current: RepoResult<T>) -> RepoResult<T>,
    ): Job =
        viewModelScope.launch {
            val current = _uiState.value
            if (emitProcessing) {
                emitProcessing()
            }
            _uiState.update { block(current) }
        }

    protected fun emitState(value: RepoResult<T>) {
        _uiState.update { value }
    }

    protected suspend fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.update { RepoResult.Success(value) }
        }
    }

    private fun emitEmpty() {
        _uiState.update { RepoResult.Empty }
    }

    private fun emitProcessing() {
        _uiState.update { RepoResult.Processing }
    }

    protected fun emitFailure(e: Throwable) {
        _uiState.update { RepoResult.Error(e) }
    }
}