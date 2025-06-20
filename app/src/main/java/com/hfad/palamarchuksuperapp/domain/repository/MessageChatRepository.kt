package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import kotlinx.coroutines.flow.Flow

interface MessageChatRepository {

    // Методы для работы с чатами
    suspend fun getAllChats(): AppResult<List<MessageChat>, AppError>
    suspend fun getAllChatsInfo(): AppResult<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): AppResult<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): AppResult<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChatEntity): AppResult<Long, AppError>
    suspend fun deleteChat(chatId: Int): AppResult<Unit, AppError>
    suspend fun clearAllChats(): AppResult<Unit, AppError>
    suspend fun isChatExist(chatId: Int): AppResult<Boolean, AppError>
}