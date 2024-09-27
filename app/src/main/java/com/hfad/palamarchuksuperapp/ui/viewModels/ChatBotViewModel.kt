package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqContentBuilder
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatBotViewModel @Inject constructor(
    val groqApi: GroqApiHandler
) : GenericViewModel<String, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    sealed class Event : BaseEvent {
        object SendImage : Event()
        data class SentText(val text: String) : Event()
    }

    sealed class Effect : BaseEffect {

    }

    val message_flow = groqApi.chatHistory
    override val _dataFlow: Flow<Result<String, DataError>> = emptyFlow()
    override val _errorFlow: MutableSharedFlow<Exception?>
        get() = TODO("Not yet implemented")
    override val uiState: StateFlow<State<String>>
        get() = TODO("Not yet implemented")

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> {  }
            is Event.SentText -> sendText(event.text)
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch {
            val request = GroqContentBuilder.Builder().let {
                it.role = "user"
                //it.text(text)
                it.buildText(text)
            }
            groqApi.sendMessageChatImage(request)
        }
    }
}