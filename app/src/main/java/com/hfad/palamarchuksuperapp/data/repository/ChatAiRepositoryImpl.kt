package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
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

    override suspend fun getAllChats(): Result<List<MessageChat>, AppError> {

        val allChatResult = withSqlErrorHandling {
            messageChatDao.getAllChatsWithMessages().map { MessageChat.from(it) }
        }

        return allChatResult
    }

    override suspend fun getAllChatsInfo(): Result<List<MessageChat>, AppError> {

        val allChatInfoResult = withSqlErrorHandling {
            messageChatDao.getAllChatsInfo().map {
                MessageChat.from(it)
            }
        }

        return allChatInfoResult
    }

    override suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError> {
        val chatWithMessagesResult = withSqlErrorHandling {
            if (messageChatDao.getAllChatsInfo().find { it.id == chatId } == null
            ) {
                Log.d("ChatAiRepositoryImpl", "getChatById: $chatId")
                val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
                val chatEntity = messageChatDao.getChatWithMessages(id.toInt())
                MessageChat.from(chatEntity)
            } else {
                val chatEntity = messageChatDao.getChatWithMessages(chatId)
                MessageChat.from(chatEntity)
            }
        }

        return chatWithMessagesResult
    }

    override suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError> {
        val chatWithMessageFlowResult = withSqlErrorHandling {
            messageChatDao
                .chatWithMessagesFlow(chatId = chatId).map { chatWithMessagesEntity ->
                    MessageChat.from(chatWithMessagesEntity)
                }
        }
        return chatWithMessageFlowResult
    }

    override suspend fun getMessageGroup(
        chatId: Int,
    ): Result<MessageGroup, AppError> {
        val messageGroupResult = withSqlErrorHandling {
            MessageGroup.from(messageChatDao.getMessageGroup(chatId))
        }
        return messageGroupResult
    }

    override suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.insertChat(
                MessageChatEntity.from(emptyChat)
            )
        }
    }

    override suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError> {
        val chatIdResult = withSqlErrorHandling {
            messageChatDao.insertChat(
                MessageChatEntity.from(chat)
            )
        }

        return chatIdResult
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
        val messageGroupIdResult = withSqlErrorHandling {
            messageChatDao.insertMessageGroupWithMessages(
                MessageGroupWithMessagesEntity.from(messageGroupWithChatID)
            )
        }
        return messageGroupIdResult
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
            MessageAI.toDomainModel(
                messageChatDao.insertAndReturnMessage(MessageAiEntity.from(messageAI))
            )
        }
    }

    override suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageChatDao.updateMessage(
                MessageAiEntity(
                    id = messageAI.id,
                    messageGroupId = messageAI.messageGroupId,
                    timestamp = messageAI.timestamp,
                    message = messageAI.message,
                    otherContent = messageAI.otherContent?.toString(),
                    model = messageAI.model,
                    isChosen = messageAI.isChosen,
                    status = messageAI.status
                )
            )
        }
    }

    override suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError> {
        val messageAiEntity = withSqlErrorHandling {
            MessageAI.toDomainModel(
                messageChatDao.insertAndReturnMessage(
                    MessageAiEntity.from(messageAI)
                )
            )
        }
        return messageAiEntity
    }

    override suspend fun getAllMessagesWithStatus(
        chatId: Int,
        status: MessageStatus,
    ): Result<List<MessageAI>, AppError> {
        val loadingMessagesResult = withSqlErrorHandling {
            messageChatDao.getMessagesWithStatus(chatId, status.name)
                .map { MessageAI.toDomainModel(it) }
        }
        return loadingMessagesResult
    }
}