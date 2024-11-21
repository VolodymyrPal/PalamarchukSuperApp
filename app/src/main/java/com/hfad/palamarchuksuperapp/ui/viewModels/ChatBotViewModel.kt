package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.usecases.AddAiMessageUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiChatUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetErrorUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val aiHandlerRepository: AiHandlerRepository,
    private val getAiChatUseCase: GetAiChatUseCase,
    private val addAiMessageUseCase: AddAiMessageUseCase,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val getErrorUseCase: GetErrorUseCase,
) : GenericViewModel<PersistentList<MessageAI>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    @Stable
    data class StateChat(
        val listMessage: PersistentList<MessageAI> = persistentListOf(),
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val listOfModels: PersistentList<AiModel> = persistentListOf(),
        val currentModel: AiModel = AiModel.OpenAIModels.BASE_MODEL,
    ) : State<PersistentList<MessageAI>>

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch(mainDispatcher) {
            launch(ioDispatcher) {
                getModels()
            }
            getErrorUseCase().collect { error ->
                when (error) {
                    is AppError.CustomError -> {
                        effect(Effect.ShowToast(error.error.toString() ?: "Unknown error"))
                    }

                    else -> {
                        effect(Effect.ShowToast(error.toString()))
                    }
                }
            }
        }

    }

    override val _dataFlow: Flow<Result<PersistentList<MessageAI>, AppError>> =
        getAiChatUseCase().map { Result.Success(it) }

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _errorFlow,
    ) { chatHistory, isLoading, error ->
        when (chatHistory) {
            is Result.Success -> {
                StateChat(
                    listMessage = chatHistory.data,
                    isLoading = isLoading,
                    error = error,
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

    private fun sendImage(text: String, image: Base64) {
        viewModelScope.launch(ioDispatcher) {
            _loading.update { true }
            sendChatRequestUseCase(
                MessageAI(
                    role = Role.USER,
                    content = text,
                    otherContent = image,
                    type = MessageType.IMAGE
                ),
                aiHandlerRepository.handlerList
            )
            _loading.update { false }
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch(ioDispatcher) {
            _loading.update { true }
            sendChatRequestUseCase(
                MessageAI(
                    role = Role.USER,
                    content = text,
                    type = MessageType.TEXT
                ),
                aiHandlerRepository.handlerList
            )
            _loading.update { false }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch(mainDispatcher) {
            effect(Effect.ShowToast(message))
        }
    }

    private fun changeAIModel(model: AiModel) {
        //TODO
    }

    private fun getModels() {
        viewModelScope.launch(ioDispatcher) {
        }
    }
}