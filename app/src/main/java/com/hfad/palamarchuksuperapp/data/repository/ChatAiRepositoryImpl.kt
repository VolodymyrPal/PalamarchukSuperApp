package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatAiRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : ChatAiRepository {

    override suspend fun getAllChats(): List<MessageChat> {
        return messageChatDao.getAllChatsWithMessages()
            .map { MessageChat.from(it) }
    }

    override suspend fun getAllChatsInfo(): Flow<List<MessageChat>> {
        return messageChatDao.getAllChatsInfo().map {
            it.map { MessageChat.from(it) }
        }
    }

    override suspend fun getChatWithMessagesById(chatId: Int): MessageChat {
        if (messageChatDao.getAllChatsInfo().find { it.id == chatId } == null) {
            Log.d("ChatAiRepositoryImpl", "getChatById: $chatId")
            val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
            return MessageChatWithRelationsEntity.toDomain(messageChatDao.getChatWithMessages(id.toInt()))
        } else {
            return MessageChatWithRelationsEntity.toDomain(messageChatDao.getChatWithMessages(chatId))
        }
    }

    override suspend fun getChatFlowById(chatId: Int): Flow<MessageChat> {
        val chatWithMessageFlow = messageChatDao
            .chatWithMessagesFlow(chatId = chatId).map { chatWithMessagesEntity ->
                MessageChat.from(chatWithMessagesEntity)
            }
        return chatWithMessageFlow
    }

    override suspend fun getMessageGroupWithMessagesById(chatId: Int): MessageGroupWithMessagesEntity {
        return messageChatDao.getMessageGroup(chatId)
    }

    override suspend fun createChat(emptyChat: MessageChat) {
        messageChatDao.insertChat(
            MessageChatEntity.from(emptyChat)
        )
    }

    override suspend fun addChatWithMessages(chat: MessageChat): Long {
        return messageChatDao.insertChatWithRelations(
            MessageChatWithRelationsEntity.from(chat)
        )
    }

    override suspend fun deleteChat(chatId: Int) {
        val chat = messageChatDao.getAllChatsInfo().find { it.id == chatId }
        chat?.let { messageChatDao.deleteChat(it) }
    }

    override suspend fun clearAllChats() {
        messageChatDao.getAllChatsInfo().forEach { messageChatDao.deleteChat(it) }
    }

    override suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Long {
        val messageGroupId = messageChatDao.insertMessageGroupWithMessages(
            MessageGroupWithMessagesEntity.from(messageGroupWithChatID)
        )
        return messageGroupId
    }

    override suspend fun updateMessageGroup(messageGroup: MessageGroup) {
        messageChatDao.updateMessageGroupWithContent(
            MessageGroupWithMessagesEntity.from(messageGroup)
        )
    }

    override suspend fun updateMessageAi(messageAI: MessageAI) {
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

    override suspend fun addAndGetMessageAi(messageAI: MessageAI): MessageAI {
        val messageEntity = messageChatDao.insertAndReturnMessage(
            MessageAiEntity.from(messageAI)
        )
        return MessageAI.toDomainModel(messageEntity)
    }
}