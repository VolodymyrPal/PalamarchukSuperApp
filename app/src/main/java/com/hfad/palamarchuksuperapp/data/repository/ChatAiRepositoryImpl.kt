package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatAiRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : ChatAiRepository {

    override suspend fun getAllChats(): Result<List<MessageChat>, AppError> {

        return executeDBWithAppErrorHandling {
            messageChatDao.getAllChatsWithMessages().map { MessageChat.from(it) }
        }

    }

    override suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.getAllChatsInfo().map {
                it.map { MessageChat.from(it) }
            }
        }
    }

    override suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError> {
        return executeDBWithAppErrorHandling {
            if (messageChatDao.getAllChatsInfo().first()
                    .find { it.id == chatId } == null
            ) { //TODO first() is blocking
                Log.d("ChatAiRepositoryImpl", "getChatById: $chatId")
                val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
                MessageChatWithRelationsEntity.toDomain(messageChatDao.getChatWithMessages(id.toInt()))
            } else {
                MessageChatWithRelationsEntity.toDomain(messageChatDao.getChatWithMessages(chatId))
            }


        }
    }

    override suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError> {
        val chatWithMessageFlow = executeDBWithAppErrorHandling {
            messageChatDao
                .chatWithMessagesFlow(chatId = chatId).map { chatWithMessagesEntity ->
                    MessageChat.from(chatWithMessagesEntity)
                }
        }
        return chatWithMessageFlow
    }

    override suspend fun getMessageGroup(
        chatId: Int,
    ): Result<MessageGroup, AppError> {
        return executeDBWithAppErrorHandling {
            MessageGroup.from(messageChatDao.getMessageGroup(chatId))
        }
    }

    override suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.insertChat(
                MessageChatEntity.from(emptyChat)
            )
        }
    }

    override suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.insertChatWithRelations(
                MessageChatWithRelationsEntity.from(chat)
            )
        }
    }

    override suspend fun deleteChat(chatId: Int): Result<Unit, AppError> {
        return executeDBWithAppErrorHandling {
            val chat = messageChatDao.getAllChatsInfo().first()
                .find { it.id == chatId } //TODO first() is blocking fun
            chat?.let { messageChatDao.deleteChat(it) }
        }
    }

    override suspend fun clearAllChats(): Result<Unit, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.getAllChatsInfo().first()
                .forEach { messageChatDao.deleteChat(it) } //TODO first() is blocking fun
        }
    }

    override suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Result<Long, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.insertMessageGroupWithMessages(
                MessageGroupWithMessagesEntity.from(messageGroupWithChatID)
            )
        }
    }

    override suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError> {
        return executeDBWithAppErrorHandling {
            messageChatDao.updateMessageGroupWithContent(
                MessageGroupWithMessagesEntity.from(messageGroup)
            )
        }
    }

    override suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError> {
        return executeDBWithAppErrorHandling {
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
        val messageEntity = executeDBWithAppErrorHandling {
            MessageAI.toDomainModel(
                messageChatDao.insertAndReturnMessage(
                    MessageAiEntity.from(messageAI)
                )
            )
        }
        return messageEntity
    }
}