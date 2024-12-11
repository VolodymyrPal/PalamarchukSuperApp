package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiChatUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiHandlersUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetErrorUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
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

@Suppress("LongParameterList")
class ChatBotViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val getAiHandlersUseCase: GetAiHandlersUseCase,
    private val getAiChatUseCase: GetAiChatUseCase,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val getErrorUseCase: GetErrorUseCase,
    private val chooseMessageAiUseCase: ChooseMessageAiUseCase,
    private val aiHandlerRepository: AiHandlerRepository,
) : GenericViewModel<PersistentList<MessageAI>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    init {
        viewModelScope.launch(mainDispatcher) {
            launch(mainDispatcher) {
                getErrorUseCase().collect { error ->
                    when (error) { //TODO Better error handler
                        is AppError.CustomError -> {
                            effect(Effect.ShowToast(error.error.toString()))
                        }

                        else -> {
                            effect(Effect.ShowToast(error.toString()))
                        }
                    }
                }
            }
            launch(mainDispatcher) {
                getAiHandlersUseCase().collect { handlerList ->
                    _handlers.update { handlerList }
                }
            }
        }
    }

    @Stable
    data class StateChat(
        val listMessage: PersistentList<MessageAI> = persistentListOf(),
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val listHandler: PersistentList<AiModelHandler> = persistentListOf(),
        val currentModel: AiModel = AiModel.OPENAI_BASE_MODEL,
    ) : State<PersistentList<MessageAI>>

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val _dataFlow: Flow<Result<PersistentList<MessageAI>, AppError>> =
        getAiChatUseCase().map { Result.Success(it) }
    private val _handlers: MutableStateFlow<List<AiModelHandler>> = MutableStateFlow(emptyList())

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _handlers,
        _errorFlow,
    ) { chatHistory, isLoading, handlers, error ->
        when (chatHistory) {
            is Result.Success -> {
                StateChat(
                    listMessage = chatHistory.data,
                    isLoading = isLoading,
                    error = error,
                    listHandler = handlers.toPersistentList()
                )
            }

            is Result.Error -> {

                StateChat(
                    listMessage = persistentListOf(),
                    isLoading = isLoading,
                    error = error,
                    listHandler = handlers.toPersistentList()
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
        data class ChooseSubMessage(val messageAiIndex: Int, val subMessageIndex: Int) : Event()
        data class UpdateHandler(val handler: AiModelHandler, val aiHandlerInfo: AiHandlerInfo) :
            Event()
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
            is Event.ChooseSubMessage -> chooseSubMessage(
                event.messageAiIndex,
                event.subMessageIndex
            )

            is Event.UpdateHandler -> updateHandler(event.handler, event.aiHandlerInfo)
        }
    }

    private fun sendImage(text: String, image: Base64) {
        viewModelScope.launch(ioDispatcher) {
            _loading.update { true }
            sendChatRequestUseCase(
                MessageAI(
                    id = getAiChatUseCase().value.size,
                    role = Role.USER,
                    content = text,
                    otherContent = image,
                    type = MessageType.IMAGE,
                ),
                handlers = _handlers.value
            )
            _loading.update { false }
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch(ioDispatcher) {
            _loading.update { true }
            sendChatRequestUseCase(
                MessageAI(
                    id = getAiChatUseCase().value.size,
                    role = Role.USER,
                    content = text,
                    type = MessageType.TEXT
                ),
                handlers = _handlers.value
            )
            _loading.update { false }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch(mainDispatcher) {
            effect(Effect.ShowToast(message))
        }
    }

    private fun chooseSubMessage(messageAiIndex: Int, subMessageIndex: Int) {
        viewModelScope.launch(ioDispatcher) {
            chooseMessageAiUseCase(messageAiIndex, subMessageIndex)
        }
    }

    private fun getModels() {
        viewModelScope.launch(ioDispatcher) {
        }
    }

    private fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        Log.d("ViewModel: ", "updateHandler: $aiHandlerInfo")
        viewModelScope.launch(ioDispatcher) {
            aiHandlerRepository.updateHandler(handler, aiHandlerInfo)
        }
    }
}