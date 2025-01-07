package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessages
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatAiRepository {
    val listChatAiFLow: StateFlow<PersistentList<MessageChat>>
    val chatAiFlow: MutableStateFlow<PersistentList<MessageGroup>>
    val errorFlow: MutableSharedFlow<AppError?>

    // Методы для работы с моделями AI
    suspend fun getModels(handler: AiModelHandler): List<AiModel>
    suspend fun getModels(handlers: List<AiModelHandler>): List<AiModel>

    // Методы для работы с чатами
    suspend fun getAllChats(): List<MessageChat>
    suspend fun getChatById(chatId: Int): MessageChat
    suspend fun createChat(chat: MessageChat)
    suspend fun deleteChat(chatId: Int)
    suspend fun clearAllChats()

    // Методы для работы с сообщениями
    suspend fun getMessageGroupById(chatId: Int): MessageGroupWithMessages
    suspend fun addMessageGroup(chatId: Int, messageGroup: MessageGroup)
    suspend fun updateMessageGroup(messageGroup: MessageGroup)
    suspend fun updateSubMessage(messageAI: MessageAI)
}