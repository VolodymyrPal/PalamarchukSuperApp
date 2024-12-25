package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.AddAiHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.DeleteAiHandlerUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiChatUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetAiHandlersUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetErrorUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.UpdateAiHandlerUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val updateAiHandlerUseCase: UpdateAiHandlerUseCase,
    private val addAiHandlerUseCase: AddAiHandlerUseCase,
    private val deleteAiHandlerUseCase: DeleteAiHandlerUseCase,
    private val getModelsUseCase: GetModelsUseCase,
) : GenericViewModel<PersistentList<MessageAI>, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    init {
        viewModelScope.launch(mainDispatcher) {
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
    }

    @Stable
    data class StateChat(
        val listMessage: PersistentList<MessageAI> = persistentListOf(),
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val listHandler: PersistentList<AiModelHandler> = persistentListOf(),
        val modelList: PersistentList<AiModel>,
    ) : State<PersistentList<MessageAI>>

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val _dataFlow: StateFlow<PersistentList<MessageAI>> = getAiChatUseCase()
    private val _handlers: StateFlow<List<AiModelHandler>> = getAiHandlersUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _choosenAiModelList = MutableStateFlow<PersistentList<AiModel>>(persistentListOf())

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _handlers,
        _errorFlow,
        _choosenAiModelList
    ) { chatHistory, isLoading, handlers, error, modelList ->
        StateChat(
            listMessage = chatHistory,
            isLoading = isLoading,
            error = error,
            listHandler = handlers.toPersistentList(),
            modelList = modelList
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StateChat(
            listMessage = persistentListOf(),
            isLoading = false,
            error = null,
            modelList = persistentListOf()
        )
    )

    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
        data class SendText(val text: String) : Event()
        data class ShowToast(val message: String) : Event()
        data class GetModels(val llmName: LLMName) : Event()
        data class ChooseSubMessage(val messageAiIndex: Int, val subMessageIndex: Int) : Event()
        data class UpdateHandler(
            val handler: AiModelHandler,
            val aiHandlerInfo: AiHandlerInfo,
        ) : Event()

        data class AddAiHandler(val aiHandlerInfo: AiHandlerInfo) : Event()
        data class DeleteHandler(val handler: AiModelHandler) : Event()
    }

    sealed class Effect : BaseEffect {
        data class ShowToast(val text: String) : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.SendImage -> sendImage(event.text, event.image)
            is Event.SendText -> sendText(event.text)
            is Event.ShowToast -> showToast(event.message)
            is Event.GetModels -> getModels(event.llmName)
            is Event.ChooseSubMessage -> chooseSubMessage(
                event.messageAiIndex,
                event.subMessageIndex
            )
            is Event.UpdateHandler -> updateOrAddHandler(event.handler, event.aiHandlerInfo)
            is Event.AddAiHandler -> addAiHandler(event.aiHandlerInfo)
            is Event.DeleteHandler -> deleteHandler(event.handler)
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
    val handler = CoroutineExceptionHandler { context, exception ->
        Log.e("CoroutineExceptionHandler", "Необработанное исключение: $exception", exception)
        // Дополнительная обработка исключения, например, показ сообщения пользователю
    }

    private fun sendText(text: String) {
        viewModelScope.launch(ioDispatcher + handler) {
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

    private fun getModels(llmName: LLMName) {
        viewModelScope.launch {
            _choosenAiModelList.update { persistentListOf() }
            val resultModels = getModelsUseCase(llmName)
            if (resultModels is Result.Success) {
                _choosenAiModelList.update { resultModels.data.toPersistentList() }
            } else { //TODO better error handling
                _choosenAiModelList.update { persistentListOf() }
            }
        }
    }

    private fun updateOrAddHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        viewModelScope.launch(ioDispatcher) {
            updateAiHandlerUseCase(handler, aiHandlerInfo)
        }
    }

    private fun addAiHandler(aiHandlerInfo: AiHandlerInfo) {
        viewModelScope.launch(ioDispatcher) {
            addAiHandlerUseCase(aiHandlerInfo)
        }
    }

    private fun deleteHandler(handler: AiModelHandler) {
        viewModelScope.launch(ioDispatcher) {
            deleteAiHandlerUseCase(handler)
        }
    }
}