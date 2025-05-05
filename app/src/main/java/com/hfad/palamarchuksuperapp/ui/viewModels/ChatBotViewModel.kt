package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.State
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.data.services.Base64
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import com.hfad.palamarchuksuperapp.domain.usecases.ChooseMessageAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveAllChatsInfoUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.ObserveChatAiUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.SendChatRequestUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.getOrHandleAppError
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LongParameterList")
class ChatBotViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val aiHandlerRepository: AiHandlerRepository,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val chooseMessageAiUseCase: ChooseMessageAiUseCase,
    private val getModelsUseCase: GetModelsUseCase,
    private val observeChatAiUseCase: ObserveChatAiUseCase,
    private val observeAllChatsInfoUseCase: ObserveAllChatsInfoUseCase,
    private val chatController: ChatController,
    private val dataStoreHandler: DataStoreHandler,
) : GenericViewModel<MessageChat, ChatBotViewModel.Event, ChatBotViewModel.Effect>() {

    private val currentChatId: StateFlow<Int> = dataStoreHandler.getCurrentChatId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("CoroutineExceptionHandler", "Необработанное исключение: $exception", exception)
        viewModelScope.launch(mainDispatcher) {
            effect(Effect.ShowToast(exception.message ?: "Произошла ошибка"))
        }
    }

    private val ioCoroutineDispatcher = ioDispatcher + handler

    @OptIn(ExperimentalCoroutinesApi::class)
    override val _dataFlow: StateFlow<MessageChat> = currentChatId.flatMapLatest { chatId ->
        val observedChat = observeChatAiUseCase.invoke(chatId)
        if (observedChat is Result.Success) {
            val chat = observedChat.data
            if (chatId != currentChatId.value) {
                dataStoreHandler.setCurrentChatId(chatId)
                _loading.update { false }
            }
//            chat.take(1).collect { chat ->
//                if (chat.id != currentChatId.value) {
//                    dataStoreHandler.setCurrentChatId(chat.id)
//                    _loading.update { false }
//                }
//            }
            observedChat.data
        } else {
            _errorFlow.emit(AppError.CustomError("Chat not found"))
            emptyFlow()
        }
    }
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MessageChat()
        )

    private val _handlers: StateFlow<PersistentList<AiModelHandler>> =
        aiHandlerRepository.aiHandlerFlow.map { it.toPersistentList() }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            persistentListOf()
        )
    private val _choosenAiModelList = MutableStateFlow<PersistentList<AiModel>>(persistentListOf())

    private val allChatInfo: StateFlow<List<MessageChat>> = flow {
        val chatList = observeAllChatsInfoUseCase.invoke()
        when (chatList) {
            is Result.Success -> chatList.data.collect {
                emit(it)
            }

            is Result.Error -> _errorFlow.emit(chatList.error)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    override val uiState: StateFlow<StateChat> = combine(
        _dataFlow,
        _loading,
        _handlers,
        _errorFlow,
        _choosenAiModelList,
    ) { chat, isLoading, handlers, error, modelList ->
        error.let { _loading.update { false } }
        StateChat(
            chat = chat,
            isLoading = isLoading,
            error = error,
            listHandler = handlers.toPersistentList(),
            modelList = modelList,
        )
    }.combine(allChatInfo) { stateChat, chatList ->
        stateChat.copy(chatList = chatList.toPersistentList())
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
        data class SelectChat(val chatId: Int) : Event()
        object CreateNewChat : Event()
        object ClearAllChats : Event()
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
            is Event.CreateNewChat -> createNewChat()
            is Event.ClearAllChats -> clearAllChats()
        }
    }

    private fun selectChat(chatId: Int) {
        viewModelScope.launch(ioCoroutineDispatcher) {
            dataStoreHandler.setCurrentChatId(chatId)
        }
    }

    private fun sendImage(text: String, image: Base64) {
//        viewModelScope.launch(ioDispatcher) {
//            _loading.update { true }
//            val chatId = currentChatId.value
//            sendChatRequestUseCase(
//                message = MessageGroup(
//                    id = 1, //TODO need to check when id updates
//                    role = Role.USER,
//                    content = text,
//                    otherContent = image,
//                    type = MessageType.IMAGE,
//                    chatGroupId = chatId
//                ),
//                handlers = _handlers.value
//            )
//            _loading.update { false }
//        }
    }


    private fun sendText(text: String) {
        viewModelScope.launch(ioCoroutineDispatcher) {
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
            ).getOrHandleAppError {
                effect(Effect.ShowToast(it.message ?: "Undefined error."))
                _loading.update { false }
                return@launch
            }
            _loading.update { false }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch(mainDispatcher) {
            effect(Effect.ShowToast(message))
        }
    }

    private fun chooseSubMessage(messageAI: MessageAI) {
        viewModelScope.launch(ioCoroutineDispatcher) {
            chooseMessageAiUseCase(messageAI).getOrHandleAppError {
                _errorFlow.emit(it)
                return@launch
            }
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
        viewModelScope.launch(ioCoroutineDispatcher) {
            aiHandlerRepository.updateHandler(handler, aiHandlerInfo)
        }
    }

    private fun addAiHandler(aiHandlerInfo: AiHandlerInfo) {
        viewModelScope.launch(ioCoroutineDispatcher) {
            aiHandlerRepository.addHandler(aiHandlerInfo)
        }
    }

    private fun deleteHandler(handler: AiModelHandler) {
        viewModelScope.launch(ioCoroutineDispatcher) {
            aiHandlerRepository.removeHandler(handler)
        }
    }

    private fun clearAllChats() = viewModelScope.launch(ioDispatcher) {
        chatController.clearAllChats()
        dataStoreHandler.setCurrentChatId(0)
    }

    private fun createNewChat() = viewModelScope.launch(ioDispatcher) {
        val newChatId = chatController.addChatWithMessages(
            MessageChat(
                name = "Base chat",
//                messageGroups = MockChat() //TODO for testing only
            )
        ).getOrHandleAppError { return@launch }.toInt()
        dataStoreHandler.setCurrentChatId(newChatId)
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