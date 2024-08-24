package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

    protected val _isRefresh = MutableStateFlow(false)

    abstract fun getData(): T

    init {
        Log.d("generic view model", "init")
    }

    private val _uiState: MutableStateFlow<State<T>> = MutableStateFlow(State.Empty(loading = true))
    override val uiState: StateFlow<State<T>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State.Empty(loading = true)
        )

    private val effectFlow = MutableSharedFlow<EFFECT>()
    override val effect: SharedFlow<EFFECT> =
        effectFlow.asSharedFlow()

    abstract fun refresh(): T

    val stateUi: StateFlow<State<T>> by lazy {
        combine(uiState, _isRefresh) { ui, isRefresh ->
            refresh()
            if (isRefresh) {
                emitRefresh(refresh())
            }
            when (ui) {
                is State.Success -> {
                    emitState(ui.copy(refreshing = isRefresh))
                    ui
                }

                else -> {
                    ui
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = State.Empty(loading = true)
        )
    }

    protected fun emitRefresh(data: T) {
        viewModelScope.launch {
            emitState(data)
        }
        _isRefresh.update { false }
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

    protected suspend fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.update { State.Success(value) }
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

    data class Empty(val loading: Boolean = true) : State<Nothing>
}