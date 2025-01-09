package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatAiRepository {
    val listChatAiFLow: StateFlow<PersistentList<MessageChat>>
    val errorFlow: MutableSharedFlow<AppError?>

    // Методы для работы с моделями AI
    suspend fun getModels(handler: AiModelHandler): List<AiModel>

    // Методы для работы с чатами
    suspend fun getAllChats(): List<MessageChat>
    suspend fun getChatById(chatId: Int): MessageChat
    suspend fun createChat(chat: MessageChat)
    suspend fun deleteChat(chatId: Int)
    suspend fun clearAllChats()

    // Методы для работы с сообщениями
    suspend fun getMessageGroupById(chatId: Int): MessageGroupWithMessagesEntity
    suspend fun addMessageGroup(messageGroup: MessageGroup)
    suspend fun addMessageGroup(chatId: Int, messageGroup: MessageGroup)
    suspend fun updateMessageGroup(messageGroup: MessageGroup)
    suspend fun updateSubMessage(messageAI: MessageAI)
}