package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatAiRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : ChatAiRepository {

    // Методы для работы с чатами
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
                val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
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
                .chatWithMessagesFlow(chatId = chatId).map { chatWithMessagesEntity ->
                    if (chatWithMessagesEntity == null) { //When we clear all chats, flow still emit null.
                        MessageChat()
                    } else {
                        MessageChat.from(chatWithMessagesEntity)
                    }
                }

        }
    }

    override suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.insertChat(MessageChatEntity.from(emptyChat))
        }
    }

    override suspend fun addAndGetChat(chat: MessageChat): Result<MessageChat, AppError> {
        return withSqlErrorHandling {
            val messageToInsert = MessageChatWithRelationsEntity.from(chat)
            val insertedMessage = messageChatDao.insertAndReturnChat(messageToInsert)
            MessageChat.from(insertedMessage)

        }
    }

    override suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError> {
        return withSqlErrorHandling {
            messageChatDao.insertChat(MessageChatEntity.from(chat))
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

    // Методы для работы с группами сообщений
    override suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError> {
        return withSqlErrorHandling {
            MessageGroup.from(messageChatDao.getMessageGroup(chatId))
        }
    }

    override suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Result<Long, AppError> {
        return withSqlErrorHandling {
            messageChatDao.insertMessageGroupWithMessages(
                MessageGroupWithMessagesEntity.from(messageGroupWithChatID)
            )
        }
    }

    override suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.updateMessageGroupWithContent(
                MessageGroupWithMessagesEntity.from(messageGroup)
            )
        }
    }

    // Методы для работы с сообщениями
    override suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError> {
        return withSqlErrorHandling {
            val messageAiEntity =
                messageChatDao.insertAndReturnMessage(MessageAiEntity.from(messageAI))
            MessageAI.from(messageAiEntity)
        }
    }

    override suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.updateMessage(
                MessageAiEntity.from(messageAI)
            )
        }
    }

    override suspend fun getAllMessagesWithStatus(
        chatId: Int,
        status: MessageStatus,
    ): Result<List<MessageAI>, AppError> {
        return withSqlErrorHandling {
            messageChatDao.getMessagesWithStatus(chatId, status.name)
                .map { MessageAI.from(it) }
        }
    }
}