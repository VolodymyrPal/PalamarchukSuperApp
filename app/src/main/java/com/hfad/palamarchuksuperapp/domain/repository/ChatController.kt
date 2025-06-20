package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import kotlinx.coroutines.flow.Flow

interface ChatController {
    suspend fun addAndGetMessageAi(messageAI: MessageAI): AppResult<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): AppResult<Unit, AppError>
    suspend fun getAllMessagesWithStatus(
        chatId: Int, status: MessageStatus,
    ): AppResult<List<MessageAI>, AppError>

    suspend fun getAllChatsInfo(): AppResult<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): AppResult<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): AppResult<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChatEntity): AppResult<Long, AppError>
    suspend fun addChatWithMessages(chat: MessageChat): AppResult<Long, AppError>
    suspend fun deleteChat(chatId: Int): AppResult<Unit, AppError>
    suspend fun clearAllChats(): AppResult<Unit, AppError>
    suspend fun isChatExist(chatId: Int): AppResult<Boolean, AppError>
    suspend fun getMessageGroup(chatId: Int): AppResult<MessageGroup, AppError>
    suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): AppResult<Long, AppError>
    suspend fun updateMessageGroup(messageGroup: MessageGroup): AppResult<Unit, AppError>
}