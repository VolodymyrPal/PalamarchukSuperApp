package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface ChatAiRepository {
    val chatAiChatFlow: MutableStateFlow<PersistentList<MessageGroup>>
    val errorFlow: MutableSharedFlow<AppError?>
    suspend fun getModels(handler: AiModelHandler): List<AiModel>
    suspend fun getModels(handlers: List<AiModelHandler>): List<AiModel>
    suspend fun addMessage(messageGroup: MessageGroup)
    suspend fun updateMessage(index: Int, updatedContent: MessageGroup)
    suspend fun updateSubMessages(index: Int, subMessageList: PersistentList<MessageAI>)
    suspend fun updateSubMessage(index: Int, messageAI: MessageAI, indexSubMessage: Int)
    suspend fun updateChat(listOfMessageGroup: PersistentList<MessageGroup>)
}