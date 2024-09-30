package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.launch

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

    protected abstract val _dataFlow: Flow<Result<T, DataError>>

    protected abstract val _errorFlow: MutableSharedFlow<DataError?>

    protected open val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    abstract override fun event(event: EVENT)
    abstract override val uiState: StateFlow<State<T>>

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

interface State<out T>