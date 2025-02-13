package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageChatRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : MessageChatRepository {

    override suspend fun getAllChats(): Result<List<MessageChat>, AppError> {
        return withSqlErrorHandling {
            messageChatDao.getAllChatsWithMessages().map { MessageChat.from(it) }
        }
    }

    override suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError> {
        return withSqlErrorHandling {
            messageChatDao.getAllChatsInfo().map { it.map { MessageChat.from(it) } }
        }
    }

    override suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError> {
        return withSqlErrorHandling {
            if (!messageChatDao.isExist(chatId)) {
                val id = messageChatDao.addChat(MessageChatEntity(name = "Base chat"))
                val chatEntity = messageChatDao.getChatWithMessages(id.toInt())
                MessageChat.from(chatEntity)
            } else {
                val chatEntity = messageChatDao.getChatWithMessages(chatId)
                MessageChat.from(chatEntity)
            }
        }
    }

    override suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError> {
        return withSqlErrorHandling {
            messageChatDao
                .observeChatWithMessagesFlow(chatId = chatId).map { chatWithMessagesEntity ->
                    if (chatWithMessagesEntity == null) { //When we clear all chats, flow still emit null.
                        MessageChat()
                    } else {
                        MessageChat.from(chatWithMessagesEntity)
                    }
                }

        }
    }

    override suspend fun createChat(emptyChat: MessageChatEntity): Result<Long, AppError> {
        return withSqlErrorHandling {
            messageChatDao.addChat(emptyChat)
        }
    }

    override suspend fun deleteChat(chatId: Int): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.deleteChat(chatId)
        }
    }

    override suspend fun clearAllChats(): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.deleteAllChats()
        }
    }

    override suspend fun isChatExist(chatId: Int): Result<Boolean, AppError> {
        return withSqlErrorHandling {
            messageChatDao.isExist(chatId)
        }
    }
}