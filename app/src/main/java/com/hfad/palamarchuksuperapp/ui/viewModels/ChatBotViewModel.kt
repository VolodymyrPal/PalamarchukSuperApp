package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatBotViewModel @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : GenericViewModel<PersistentList<MessageAI>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    data class StateChat(
        val listMessage: PersistentList<MessageAI> = persistentListOf(),
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val listOfModels: PersistentList<AiModel> = persistentListOf(),
        val currentModel: AiModel = AiModel.GeminiModels.BASE_MODEL,
    ) : State<PersistentList<MessageAI>>

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            launch {
                getModels()
            }
            chatAiRepository.errorFlow.collect { error ->
                when (error) {
                   is AppError.CustomError -> {
                        effect(Effect.ShowToast(error.errorText?: "Unknown error"))
                    }

                    else -> {
                        effect(Effect.ShowToast(error.toString()))
                    }
                }
            }
        }

    }

    override val _dataFlow: Flow<Result<PersistentList<MessageAI>, AppError>> =
        chatAiRepository.chatAiChatFlow.map { Result.Success(it) }

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _errorFlow,
        chatAiRepository.listOfModels,
        chatAiRepository.currentModel
    ) { chatHistory, isLoading, error, models, currentModel ->
        when (chatHistory) {
            is Result.Success -> {
                StateChat(
                    listMessage = chatHistory.data,
                    isLoading = isLoading,
                    error = error,
                    listOfModels = models,
                    currentModel = currentModel
                )
            }

            is Result.Error -> {

                StateChat(
                    listMessage = persistentListOf(),
                    isLoading = isLoading,
                    error = error
                )
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StateChat(
            listMessage = persistentListOf(),
            isLoading = false,
            error = null
        )
    )

    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
        data class SendText(val text: String) : Event()
        data class ShowToast(val message: String) : Event()
        data object GetModels : Event()
        data class ChangeAiModel(val aiModel: AiModel) : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> sendImage(event.text, event.image)
            is Event.SendText -> sendText(event.text)
            is Event.ShowToast -> showToast(event.message)
            is Event.GetModels -> getModels()
            is Event.ChangeAiModel -> changeAIModel(event.aiModel)
        }
    }

    private fun sendImage(text: String, imageUrl: Base64) {
        viewModelScope.launch {
            _loading.update { true }
            chatAiRepository.getRespondChatOrImage(
                MessageAI(
                    role = Role.USER,
                    content = text,
                    otherContent = imageUrl,
                    type = MessageType.IMAGE
                )
            )
            _loading.update { false }
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch {
            _loading.update { true }
            chatAiRepository.getRespondChatOrImage(
                MessageAI(
                    role = Role.USER,
                    content = text,
                    type = MessageType.TEXT
                )
            )
            _loading.update { false }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            effect(Effect.ShowToast(message))
        }
    }

    private fun changeAIModel(model: AiModel) {
        viewModelScope.launch {
            chatAiRepository.setHandlerOrModel(model)
        }
    }

    private fun getModels() {
        viewModelScope.launch {
            chatAiRepository.getModels()
        }
    }
}