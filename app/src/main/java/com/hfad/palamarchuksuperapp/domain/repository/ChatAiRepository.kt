package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
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
    suspend fun updateSubMessages(index: Int, subMessageList: PersistentList<SubMessageAI>)
    suspend fun updateSubMessage(index: Int, subMessageAI: SubMessageAI, indexSubMessage: Int)
    suspend fun updateChat(listOfMessageAI: PersistentList<MessageAI>)
}