package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageChatRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : MessageChatRepository {

    override suspend fun getAllChats(): AppResult<List<MessageChat>, AppError> {
        return withSqlErrorHandling {
            messageChatDao.getAllChatsWithMessages().map { MessageChat.from(it) }
        }
    }

    override suspend fun getAllChatsInfo(): AppResult<Flow<List<MessageChat>>, AppError> {
        return withSqlErrorHandling {
            messageChatDao.getAllChatsInfo().map { it.map { MessageChat.from(it) } }
        }
    }

    override suspend fun getChatWithMessagesById(chatId: Int): AppResult<MessageChat, AppError> {
        return withSqlErrorHandling {
            if (!messageChatDao.isExist(chatId)) {
                val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
                val chatEntity = messageChatDao.getChatWithMessages(id.toInt())
                MessageChat.from(chatEntity)
            } else {
                val chatEntity = messageChatDao.getChatWithMessages(chatId)
                MessageChat.from(chatEntity)
            }
        }
    }

    override suspend fun getChatFlowById(chatId: Int): AppResult<Flow<MessageChat>, AppError> {
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

    override suspend fun createChat(emptyChat: MessageChatEntity): AppResult<Long, AppError> {
        return withSqlErrorHandling {
            messageChatDao.insertChat(emptyChat)
        }
    }

    override suspend fun deleteChat(chatId: Int): AppResult<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.deleteChat(chatId)
        }
    }

    override suspend fun clearAllChats(): AppResult<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.deleteAllChats()
        }
    }

    override suspend fun isChatExist(chatId: Int): AppResult<Boolean, AppError> {
        return withSqlErrorHandling {
            messageChatDao.isExist(chatId)
        }
    }
}