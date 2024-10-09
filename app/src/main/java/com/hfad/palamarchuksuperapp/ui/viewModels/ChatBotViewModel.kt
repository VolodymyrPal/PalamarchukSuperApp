package com.hfad.palamarchuksuperapp.ui.viewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqContentBuilder
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
        val listMessage: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val error: DataError? = null,
    ) : State<List<Message>>

    override val _errorFlow: MutableStateFlow<DataError?> = groqApi.errorFlow
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            groqApi.errorFlow.collect { error ->
                when (error) {
                    is DataError.CustomError -> {
                        effect(Effect.ShowToast(error.errorText))
                    }

                    else -> {
                        effect(Effect.ShowToast(error.toString()))
                    }
                }
            }
        }
    }

    override val _dataFlow: Flow<Result<List<Message>, DataError>> =
        groqApi.chatHistory.map<List<Message>, Result<List<Message>, DataError>> {
            Result.Success(it)
        }.catch {
            emit(Result.Error(DataError.CustomError(it.message ?: "Error")))
        }


    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow, _loading, _errorFlow
    ) { chatHistory, isLoading, error ->
        when (chatHistory) {
            is Result.Success -> {
                StateChat(
                    listMessage = chatHistory.data,
                    isLoading = isLoading,
                    error = null
                )
            }

            is Result.Error -> {
                StateChat(
                    listMessage = emptyList(),
                    isLoading = isLoading,
                    error = null
                )
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StateChat(
            listMessage = emptyList(),
            isLoading = false,
            error = null
        )
    )

    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
        data class SentText(val text: String) : Event()
        data class ShowToast(val message: String) : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> sendImage(event.text, event.image)
            is Event.SentText -> sendText(event.text)
            is Event.ShowToast -> showToast(event.message)
        }
    }

    private fun sendImage(text: String, image: String) {
        viewModelScope.launch {
            _loading.update { true }
            Log.d("sendImage first: ", "${_loading.value}")
            val request = GroqContentBuilder.Builder().let {
                it.role = "user"
                it.text(text)
                it.image("https://i.pinimg.com/736x/f7/f5/e6/f7f5e629f2f648dd12f60d2189f8d6cc.jpg")
                it.buildChat()
            }
            groqApi.getRespondChatImage(request)
            delay(1500)
            _loading.update { false }
            Log.d("sendImage second: ", "${_loading.value}")
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

    private fun showToast(message: String) {
        viewModelScope.launch {
            effect(Effect.ShowToast(message))
        }
    }
}