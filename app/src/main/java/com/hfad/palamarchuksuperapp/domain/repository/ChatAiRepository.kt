package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface ChatAiRepository {

    // Методы для работы с чатами
    suspend fun getAllChats(): Result<List<MessageChat>, AppError>
    suspend fun getAllChatsInfo(): Result<List<MessageChat>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError>
    suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError>
    suspend fun deleteChat(chatId: Int): Result<Unit, AppError>
    suspend fun clearAllChats(): Result<Unit, AppError>

    // Методы для работы с сообщениями
    suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError>
    suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Result<Long, AppError>
    suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError>
    suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError>
}