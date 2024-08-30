package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

    abstract suspend fun getData(): suspend () -> T
    abstract suspend fun getDataFlow(): Flow<T>
    abstract override fun event(event: EVENT)

    private val _uiState: MutableStateFlow<State<T>> by lazy {
        MutableStateFlow(State.Empty(loading = true))
    }

    override val uiState: StateFlow<State<T>> by lazy {
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State.Empty(loading = true)
        ).also {
            viewModelScope.launch {
                emitRefresh()
            }
        }
    }

    private val effectFlow = MutableSharedFlow<EFFECT>()
    override val effect: SharedFlow<EFFECT> =
        effectFlow.asSharedFlow()

    protected suspend fun emitRefresh() {
        emitProcessing()
        try {
            val items = getData().invoke()
            emitState(items)
        } catch (e: Exception) {
            emitFailure(e)
        }
    }

    protected fun emitState(
        emitProcessing: Boolean,
        block: suspend (current: State<T>) -> State<T>,
    ): Job =
        viewModelScope.launch {
            val current = uiState.value
            if (emitProcessing) {
                emitProcessing()
            }
            _uiState.update {
                block(current)
            }
        }

    protected fun emitState(value: State<T>) {
        _uiState.update { value }
    }

    protected fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.update {
                if (it is State.Success) it.copy(items = value, refreshing = false)
                else State.Success(value, refreshing = false)
            }
        }
    }

    private fun emitEmpty() {
        _uiState.update { state ->
            if (state is State.Success) state.copy(refreshing = false) else State.Empty(loading = false)
        }
    }

    private fun emitProcessing() {
        _uiState.update { state ->
            if (state is State.Success) {
                state.copy(refreshing = true)
            } else State.Processing
        }
    }

    protected fun emitFailure(e: Throwable) {
        _uiState.update { state ->
            if (state is State.Success) state.copy(
                refreshing = false,
                message = e.message
            ) else State.Error(e)
        }
    }
}

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val uiState: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}

sealed interface BaseEvent

sealed interface BaseEffect

sealed interface State<out T> {
    data object Processing : State<Nothing>

    data class Success<out T>(
        val items: T,
        val refreshing: Boolean = false,
        val message: String? = null,
    ) : State<T>

    data class Error(val exception: Throwable) : State<Nothing>

    data class Empty( val loading: Boolean = true) : State<Nothing>
}

/* Fetching data with error handling */
//    private suspend fun <T> retryWithBackoff(
//        times: Int = 5,
//        initialDelay: Long = 500,
//        maxDelay: Long = 6000,
//        factor: Double = 2.0,
//        block: suspend () -> T,
//    ): T {
//        var currentDelay = initialDelay
//        repeat(times - 1) {
//            try {
//                return block()
//            } catch (e: Exception) {
//                Log.d("Generic VM", "Attempt ${it + 1} failed with exception: ${e.message}")
//            }
//            delay(currentDelay)
//            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
//        }
//        return block()
//    }