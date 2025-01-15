package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface ChatAiRepository {
    val errorFlow: MutableSharedFlow<AppError?>

    // Методы для работы с моделями AI
    suspend fun getModels(handler: AiModelHandler): List<AiModel>

    // Методы для работы с чатами
    suspend fun getAllChats(): List<MessageChat>
    suspend fun getChatWithMessagesById(chatId: Int): MessageChat
    suspend fun getChatFlowById(chatId: Int): Flow<MessageChat>
    suspend fun createChat(chat: MessageChat)
    suspend fun deleteChat(chatId: Int)
    suspend fun clearAllChats()

    // Методы для работы с сообщениями
    suspend fun getMessageGroupWithMessagesById(chatId: Int): MessageGroupWithMessagesEntity
    suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Long
    suspend fun updateMessageGroup(messageGroup: MessageGroup)
    suspend fun addAndGetMessageAi(messageAI: MessageAI): MessageAI
    suspend fun updateMessageAi(messageAI: MessageAI)
}