package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveChatAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LongParameterList")
class ChatBotViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val chatAiRepository: ChatAiRepository,
    private val aiHandlerRepository: AiHandlerRepository,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val chooseMessageAiUseCase: ChooseMessageAiUseCase,
    private val getModelsUseCase: GetModelsUseCase,
    private val observeChatAiUseCase: ObserveChatAiUseCase,

    init {
        viewModelScope.launch(mainDispatcher) {
            getErrorUseCase().collect { error ->
                when (error) { //TODO Better error handler
                    is AppError.CustomError -> {
                        effect(Effect.ShowToast(error.error.toString()))
                    }

    private val currentChatId = MutableStateFlow(1)

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val _dataFlow: StateFlow<PersistentList<MessageGroup>> = getAiChatUseCase()
    private val _handlers: StateFlow<List<AiModelHandler>> = getAiHandlersUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _choosenAiModelList = MutableStateFlow<PersistentList<AiModel>>(persistentListOf())

    private val _chatList = MutableStateFlow<PersistentList<MessageChat>>(persistentListOf())

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _handlers,
        _errorFlow,
        _choosenAiModelList,
    ) { chat, isLoading, handlers, error, modelList ->
        StateChat(
            chat = chat,
            isLoading = isLoading,
            error = error,
            listHandler = handlers.toPersistentList(),
            modelList = modelList,
        )
    }.combine(_chatList) { stateChat, chatList ->
        stateChat.copy(chatList = chatList)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StateChat(
            chat = MessageChat()
        )
    )

    sealed class Event : BaseEvent {
        data class SendImage(val text: String, val image: String) : Event()
        data class SendText(val text: String) : Event()
        data class ShowToast(val message: String) : Event()
        data class GetModels(val llmName: LLMName) : Event()
        data class ChooseSubMessage(val messageAI: MessageAI) : Event()
        data class UpdateHandler(val handler: AiModelHandler, val aiHandlerInfo: AiHandlerInfo) :
            Event()

        data class AddAiHandler(val aiHandlerInfo: AiHandlerInfo) : Event()
        data class DeleteHandler(val handler: AiModelHandler) : Event()
        data object GetAllChats : Event()
        data class SelectChat(val chatId: Int) : Event()
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
            is Event.ChooseSubMessage -> chooseSubMessage(event.messageAI)
            is Event.UpdateHandler -> updateOrAddHandler(event.handler, event.aiHandlerInfo)
            is Event.AddAiHandler -> addAiHandler(event.aiHandlerInfo)
            is Event.DeleteHandler -> deleteHandler(event.handler)
            is Event.SelectChat -> selectChat(event.chatId)
            is Event.GetAllChats -> getAllChats()
        }
    }

    private fun selectChat(chatId: Int) {
        viewModelScope.launch {
            try {
                currentChatId.emit(chatId)
            } catch (e: Exception) {
                _errorFlow.emit(AppError.CustomError(e.message))
            }
        }
    }

    private fun sendImage(text: String, image: Base64) {
        viewModelScope.launch(ioDispatcher) {
            _loading.update { true }
            sendChatRequestUseCase(
                MessageGroup(
                    id = getAiChatUseCase().value.size,
                    role = Role.USER,
                    content = text,
                    otherContent = image,
                    type = MessageType.IMAGE,
                    chatGroupId = 0 // TODO
                ),
                handlers = _handlers.value
            )
            _loading.update { false }
        }
    }
    val handler = CoroutineExceptionHandler { context, exception ->
    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("CoroutineExceptionHandler", "Необработанное исключение: $exception", exception)
        viewModelScope.launch(mainDispatcher) {
            effect(Effect.ShowToast(exception.message ?: "Произошла ошибка"))
        }
    }

    private fun sendText(text: String) {
        viewModelScope.launch(ioDispatcher + handler) {
            _loading.update { true }
            val chatId = currentChatId.value
            sendChatRequestUseCase(
                message = MessageGroup(
                    id = 0,
                    role = Role.USER,
                    content = text,
                    chatId = chatId,
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

    private fun chooseSubMessage(messageAI: MessageAI) {
        viewModelScope.launch(ioDispatcher) {
            chooseMessageAiUseCase(messageAI)
        }
    }

    private fun getModels(llmName: LLMName) {
        viewModelScope.launch {
            _choosenAiModelList.update { persistentListOf() }
            when (val resultModels: Result<List<AiModel>, AppError> = getModelsUseCase(llmName)) {
                is Result.Success -> _choosenAiModelList.update { resultModels.data.toPersistentList() }
                is Result.Error -> effect(Effect.ShowToast("Не удалось загрузить модели: ${resultModels.error}"))
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

    private fun getAllChats(): List<MessageChat> {
        val chatList = mutableListOf<MessageChat>()
        viewModelScope.launch(ioDispatcher) {
            chatList.addAll(chatAiRepository.getAllChats())
        }
        return chatList
    }

    @Stable
    data class StateChat(
        val chat: MessageChat,
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val listHandler: PersistentList<AiModelHandler> = persistentListOf(),
        val modelList: PersistentList<AiModel> = persistentListOf(),
        val chatList: PersistentList<MessageChat> = persistentListOf(),
    ) : State<MessageChat>
}