package com.hfad.palamarchuksuperapp.domain.usecases

import androidx.datastore.core.DataStore
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
    val handlerSettingStore: DataStore<AiHandlersSettings>,
    private val chatAiRepository: ChatAiRepository,
) {

    val handlerList: List<AiModelHandler> =
        apiHandlers.filter { it.enabled }.sortedBy { it.modelName }

    suspend fun getCurrentHandler(currentModel: AiModel): AiModelHandler {
        return handlerList.firstOrNull { it.baseModel == currentModel }
            ?: throw IllegalArgumentException("No handler found for model $currentModel")
    }

    suspend fun getModels(): List<AiModel> {
        return chatAiRepository.getModels()
    }

    suspend fun getResponse(message: MessageAI) {
        chatAiRepository.getRespondChatOrImage(message, handlerList)
        // TODO pass handlers
    }

    fun observeChatFlow(): Flow<PersistentList<MessageAI>> {
        return chatAiRepository.chatAiChatFlow
    }

    fun observeErrors(): Flow<AppError?> {
        return chatAiRepository.errorFlow
    }

}

data class AiHandlersSettings(
    val handlers: PersistentList<String>,
)