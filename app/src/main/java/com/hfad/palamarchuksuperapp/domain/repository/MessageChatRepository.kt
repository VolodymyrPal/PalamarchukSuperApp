package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface MessageChatRepository {

    // Методы для работы с чатами
    suspend fun getAllChats(): Result<List<MessageChat>, AppError>
    suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChatEntity): Result<Long, AppError>
    suspend fun deleteChat(chatId: Int): Result<Unit, AppError>
    suspend fun clearAllChats(): Result<Unit, AppError>
    suspend fun isChatExist(chatId: Int): Result<Boolean, AppError>
}