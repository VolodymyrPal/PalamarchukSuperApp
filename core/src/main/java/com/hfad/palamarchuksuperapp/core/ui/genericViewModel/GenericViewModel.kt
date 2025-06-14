package com.hfad.palamarchuksuperapp.core.ui.genericViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class GenericViewModel<T: ScreenState, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<T, EVENT, EFFECT> {

    protected abstract val _dataFlow: Flow<Any> //Result<T, AppError>>

    protected abstract val _errorFlow: Flow<AppError?> //MutableStateFlow<AppError?>

    protected open val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    abstract override fun event(event: EVENT)
    abstract override val uiState: StateFlow<T>

    private val effectFlow = MutableSharedFlow<EFFECT>(extraBufferCapacity = 1)
    override val effect: SharedFlow<EFFECT> = effectFlow.asSharedFlow()

    override fun effect(effect: EFFECT) {
        viewModelScope.launch {
            Log.d("Generic VM", "Effect was $effect")
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

interface BaseEvent

interface BaseEffect

interface ScreenState