package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ChatController {
    suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError>
    suspend fun getAllMessagesWithStatus(
        chatId: Int, status: MessageStatus,
    ): Result<List<MessageAI>, AppError>

    suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChatEntity): Result<Long, AppError>
    suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError>
    suspend fun deleteChat(chatId: Int): Result<Unit, AppError>
    suspend fun clearAllChats(): Result<Unit, AppError>
    suspend fun isChatExist(chatId: Int): Result<Boolean, AppError>
    suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError>
    suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Result<Long, AppError>
    suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError>
}