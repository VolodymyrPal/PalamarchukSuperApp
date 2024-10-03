package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqContentBuilder
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatBotViewModel @Inject constructor(
    private val groqApi: GroqApiHandler,
) : GenericViewModel<List<Message>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    data class StateChat(
        val listMessage: List<Message>,
        val isLoading: Boolean,
        val error: DataError?,
    ) : State<List<Message>>

    override val _errorFlow: MutableSharedFlow<DataError?> = groqApi.errorFlow

    init {
        viewModelScope.launch {
            groqApi.errorFlow.collectLatest {
                effect(Effect.ShowToast(it.toString()))
            }
        }
    }

    override val _dataFlow: Flow<Result<List<Message>, DataError>> = groqApi.chatHistory.map {
        Result.Success<List<Message>, DataError>(it)
    }.catch {
        Log.d("_dataFlow: ", "Error")
        groqApi.errorFlow.emit(DataError.Network.Unknown)
        //Result.Error<List<Message>, DataError>(DataError.Network.Unknown)
    }

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow, _loading, _errorFlow
    ) { chatHistory, isLoading, error ->
        when (chatHistory) {
            is Result.Success -> {
                StateChat(
                    listMessage = chatHistory.data,
                    isLoading = isLoading,
                    error = error
                )
            }
            is Result.Error -> {
                StateChat(
                    listMessage = emptyList(),
                    isLoading = isLoading,
                    error = error
                )
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = StateChat(
            listMessage = emptyList(),
            isLoading = false,
            error = null
        )
    )

    sealed class Event : BaseEvent {
        object SendImage : Event()
        data class SentText(val text: String) : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> {}
            is Event.SentText -> sendText(event.text)
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch {
            _loading.update { true }
            val request = GroqContentBuilder.Builder().let {
                it.role = "user"
                //it.text(text)
                it.buildText(text)
            }
            groqApi.getRespondChatImage(request)
            _loading.update { false }
        }
    }
}