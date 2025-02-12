package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface MessageChatRepository {

    // Методы для работы с чатами
    suspend fun getAllChats(): Result<List<MessageChat>, AppError>
    suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError>
    suspend fun addAndGetChat(chat: MessageChat): Result<MessageChat, AppError>
    suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError>
    suspend fun deleteChat(chatId: Int): Result<Unit, AppError>
    suspend fun clearAllChats(): Result<Unit, AppError>
    suspend fun isChatExist(chatId: Int): Result<Boolean, AppError>
}