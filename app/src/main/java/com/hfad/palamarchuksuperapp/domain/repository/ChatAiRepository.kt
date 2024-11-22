package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatAiRepository {
    val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>>
    val errorFlow: MutableSharedFlow<AppError?>
    suspend fun getModels(handler: AiModelHandler): List<AiModel>
    suspend fun getModels(handlers: List<AiModelHandler>): List<AiModel>
    suspend fun addMessage(messageAI: MessageAI)
    suspend fun updateMessage(index: Int, updatedContent: MessageAI)
}