package com.hfad.palamarchuksuperapp.ui.viewModels

import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ChatBotViewModel @Inject constructor(

) : GenericViewModel<String, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    sealed class Event : BaseEvent {
        object SendImage : Event()
    }

    sealed class Effect : BaseEffect {

    }

    override val _dataFlow: Flow<Result<String, DataError>>
        get() = TODO("Not yet implemented")
    override val _errorFlow: MutableSharedFlow<Exception?>
        get() = TODO("Not yet implemented")
    override val uiState: StateFlow<State<String>>
        get() = TODO("Not yet implemented")

    override fun event(event: Event) {
        when (event) {
            Event.SendImage -> sendImage()
        }
    }

    private fun sendImage() {

    }
}