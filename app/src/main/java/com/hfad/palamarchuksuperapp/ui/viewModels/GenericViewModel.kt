package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

    protected open val _dataFlow: Flow<T> = emptyFlow()
    protected open val _errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)
    protected open val _loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    abstract override fun event(event: EVENT)
    abstract override val uiState : StateFlow<State<T>>

    private val effectFlow = MutableSharedFlow<EFFECT>()
    override val effect: SharedFlow<EFFECT> =
        effectFlow.asSharedFlow()

    override fun effect(effect: EFFECT) {
        viewModelScope.launch {
            effectFlow.emit(effect)
        }
    }
}

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val uiState: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
    fun effect(effect: EFFECT)
}

sealed interface BaseEvent

sealed interface BaseEffect

data class State<out T>(
    val items: T? = null,
    val loading: Boolean = false,
    val error: Throwable? = null,
)