package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

    private val _uiState: MutableStateFlow<State<T>> = MutableStateFlow(State.Empty)
    override val uiState: StateFlow<State<T>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State.Empty
        )

    private val effectFlow = MutableSharedFlow<EFFECT>()
    override val effect: SharedFlow<EFFECT> =
        effectFlow.asSharedFlow()

    private val refreshTrigger = DefaultRefreshTrigger()

    protected fun loadAndObserveData(
        initialData: State<T> = State.Empty,
        observeData: (T) -> Flow<T> = { emptyFlow() },
        fetchData: suspend (State<T>) -> Result<T>,
        onRefreshFailure: (Throwable) -> Unit = {}
    ): StateFlow<State<T>> = loadAndObserveData(
        initialData = initialData,
        observeData = observeData,
        fetchData = fetchData,
        onRefreshFailure = onRefreshFailure
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initialData
    )

    private fun loadAndObserveDataFlow(
        refreshTrigger: RefreshTrigger,
        initialData: State<T>,
        observeData: (T) -> Flow<T>,
        fetchData: suspend (State<T>) -> Result<T>,
        onRefreshFailure: (Throwable) -> Unit
    ): Flow<State<T>> {
        return flow {
            emit(initialData)
            refreshTrigger.refreshEvent.collect {
                emit(State.Processing)
            }
        }.flatMapLatest { currentState ->
            flow {
                emit(currentState)
                if (currentState is State.Processing) {
                    val newResult = fetchData(currentState)
                    newResult.fold(
                        onSuccess = { value ->
                            emit(State.Success(value))
                            emitAll(observeData(value).map { State.Success(it) })
                        },
                        onFailure = { exception ->
                            if (currentState is State.Success) {
                                onRefreshFailure(exception)
                                emit(State.Success(currentState.data))
                            } else {
                                emit(State.Error(exception))
                            }
                        }
                    )
                }
            }
        }.distinctUntilChanged()
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
            Log.d("TAG", "emitState: suspended emit State just update")
            _uiState.update { State.Success(value) }
        }
    }

    private fun emitEmpty() {
        _uiState.update { State.Empty }
    }

    private fun emitProcessing() {
        Log.d("TAG", "Emit Processing")
        _uiState.update { State.Processing }
    }

    protected fun emitFailure(e: Throwable) {
        _uiState.update { State.Error(e) }
    }
}

fun <T> Flow<T>.asResult(): Flow<State<T>> {
    return this
        .map<T, State<T>> { State.Success(it) }
        .onStart { emit(State.Processing) }
        .catch { emit(State.Error(it)) }
}

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val uiState: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}

sealed interface State<out T> {
    object Processing : State<Nothing>
    data class Success<out T>(val data: T) : State<T>
    data class Error(val exception: Throwable) : State<Nothing>
    object Empty : State<Nothing>
}

sealed interface BaseEvent

sealed interface BaseEffect