package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AiHandlerDispatcherUseCase @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
    //val handlerSettingStore: DataStore<AiHandlersSettings>,
    private val chatAiRepository: ChatAiRepository,
) : AiHandlerDispatcher {

    override val handlerList: List<AiModelHandler> =
        apiHandlers.filter { it.enabled }.sortedBy { it.modelName }

    /**
     * Describe how to get handlers and sort them
     */
    //MutableStateFlow(listOf(OpenAIApiHandler(HttpClient()))).value
    //handlerSettingStore.aiHandler TODO

    override suspend fun getModelsFromHandler(handler: AiModelHandler): List<AiModel> {
        return chatAiRepository.getModels(handler)
    }

    override suspend fun getResponse(message: MessageAI) {
        chatAiRepository.getRespondChatOrImage(message, handlerList)
    }

    override fun observeChatFlow(): Flow<PersistentList<MessageAI>> {
        return chatAiRepository.chatAiChatFlow
    }

    override fun observeErrors(): Flow<AppError?> {
        return chatAiRepository.errorFlow
    }

}