package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqContentBuilder
import com.hfad.palamarchuksuperapp.data.services.Message
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatBotViewModel @Inject constructor(
    private val groqApi: GroqApiHandler
) : GenericViewModel<List<Message>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    data class StateChat(
        val listMessage: List<Message>,
        val isLoading: Boolean,
        val error: DataError?
    ): State<List<Message>>

    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val uiState: StateFlow<StateChat> = combine(
        groqApi.chatHistory, isLoading
    ) { chatHistory, isLoading ->
        StateChat(
            listMessage = chatHistory,
            isLoading = isLoading,
            error = null
        )
    }.stateIn(viewModelScope,
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

    }

    override val _dataFlow: Flow<Result<List<Message>, DataError>> =  emptyFlow() //groqApi.chatHistory
    override val _errorFlow: MutableSharedFlow<DataError?> = groqApi.errorFlow

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> {  }
            is Event.SentText -> sendText(event.text)
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch {
            isLoading.update { true }
            val request = GroqContentBuilder.Builder().let {
                it.role = "user"
                //it.text(text)
                it.buildText(text)
            }
            groqApi.getRespondChatImage(request)
            isLoading.update { false }
        }
    }
}