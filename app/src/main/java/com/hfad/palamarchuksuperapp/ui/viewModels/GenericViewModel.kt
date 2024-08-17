package com.hfad.palamarchuksuperapp.ui.viewModels

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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class GenericViewModel<T, EVENT : BaseEvent, EFFECT : BaseEffect> : ViewModel(),
    UnidirectionalViewModel<State<T>, EVENT, EFFECT> {

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

    sealed class Async<out T> {
        object Loading : Async<Nothing>()
        data class Error(val errorMessage: Throwable) : Async<Nothing>()
        data class Success<out T>(val data: T) : Async<T>()
    }

    private val refreshTrigger = DefaultRefreshTrigger()

    protected fun emitState(
        emitProcessing: Boolean,
        block: suspend (current: State<T>) -> State<T>,
    ): Job =
        viewModelScope.launch {
            val current = uiState.value
            if (emitProcessing) {
                if (current is State.Success) {
                    _uiState.update { current.copy(refreshing = true) }
                } else {
                    emitProcessing()
                }
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
        _uiState.update { State.Empty(loading = false) }
    }

    private fun emitProcessing() {
        _uiState.update { State.Processing }
    }

    protected fun emitFailure(e: Throwable) {
        _uiState.update { State.Error(e) }
    }
}

interface UnidirectionalViewModel<STATE, EVENT, EFFECT> {
    val uiState: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}

sealed interface BaseEvent

sealed interface BaseEffect

fun <T> loading(): State<T> = State.Processing
fun <T> loadingSuccess(data: T): State<T> = State.Success(data)
fun <T> loadingFailure(e: Throwable): State<T> = State.Error(e)
fun <T> Result<T>.toLoadingResult() = fold(
    onSuccess = { loadingSuccess(it) },
    onFailure = { loadingFailure(it) }
)

sealed interface DataLoader<T> {

    fun loadAndObserveData(
        initialData: State<T> = State.Empty(loading = true),
        refreshTrigger: RefreshTrigger? = null,
        observeData: (T) -> Flow<T>,
        fetchData: suspend (State<T>) -> Result<T>,
    ): Flow<State<T>> = flow {

        val observe: (T) -> Flow<State<T>> = { value ->
            observeData(value).map { loadingSuccess(it) }
        }
        emit(initialData)
        when {
            initialData is State.Processing -> {
                val newData = fetchData(initialData)
                emit(newData.toLoadingResult())
                newData.onSuccess { value -> emitAll(observe(value)) }
            }

            initialData is State.Success -> {
                emitAll(observe(initialData.items))
            }

            else -> {}
        }
    }
}

fun <T> DataLoader(): DataLoader<T> = DefaultDataLoader()

private class DefaultDataLoader<T> : DataLoader<T> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun loadAndObserveData(
        initialData: State<T>,
        refreshTrigger: RefreshTrigger?,
        observeData: (T) -> Flow<T>,
        fetchData: suspend (State<T>) -> Result<T>,
    ): Flow<State<T>> {
        val refreshEventFlow =
            (refreshTrigger as? DefaultRefreshTrigger)?.refreshEvent ?: emptyFlow()

        var lastValue = initialData

        return flow {
            emit(lastValue)
            refreshEventFlow.collect {
                emit(loading())
                emit(fetchData(lastValue).toLoadingResult())
            }
        }
            .flatMapLatest { currentResult ->
                loadAndObserveData(currentResult, observeData, fetchData)
            }
            .distinctUntilChanged()
            .onEach { lastValue = it }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadAndObserveRefreshData(
        initialData: State<T>,
        observeData: (T) -> Flow<T>,
        fetchData: suspend (State<T>) -> Result<T>,
        refreshTrigger: RefreshTrigger?,
        coroutineScope: CoroutineScope,
    ): StateFlow<State<T>> {
        return flow {
            val refreshEventFlow =
                (refreshTrigger as? DefaultRefreshTrigger)?.refreshEvent ?: emptyFlow()
            refreshEventFlow.collect {
                if (initialData is State.Success) {
                    emit(initialData.copy(refreshing = true))
                } else {
                    emit(loading())
                }
                emit(fetchData(initialData).toLoadingResult())
            }
        }
            .flatMapLatest { currentResult ->
                loadAndObserveData(currentResult, observeData, fetchData)
            }.stateIn(coroutineScope, SharingStarted.Eagerly, initialData)
    }


    fun loadAndObserveData(
        initialData: State<T>,
        observeData: (T) -> Flow<T>,
        fetchData: suspend (State<T>) -> Result<T>,
    ): Flow<State<T>> = flow {
        val observe: (T) -> Flow<State<T>> =
            { value -> observeData(value).map { loadingSuccess(it) } }
        emit(initialData)
        when {
            initialData is State.Success -> emitAll(observe(initialData.items))
            else -> {}
        }
    }
}


sealed interface RefreshTrigger {
    suspend fun refresh()
    val refreshEvent: SharedFlow<Unit>
}

fun RefreshTrigger(): RefreshTrigger = DefaultRefreshTrigger()

private class DefaultRefreshTrigger : RefreshTrigger {

    private val _refreshEvent = MutableSharedFlow<Unit>()
    override val refreshEvent = _refreshEvent.asSharedFlow()

    override suspend fun refresh() {
        _refreshEvent.emit(Unit)
    }

}


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