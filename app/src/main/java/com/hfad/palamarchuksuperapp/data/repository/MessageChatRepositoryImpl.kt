package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import javax.inject.Inject

class MessageChatRepositoryImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : MessageChatRepository {

    //var currentMessageList: MessageChat? = getAllChats().firstOrNull()

    override fun getAllChats(): List<MessageChat> {
        return messageChatDao.getAllChatsWithMessages().map { it.toDomainModel() }
    }

    override suspend fun getChatById(chatId: Int): MessageChat? {
        throw NotImplementedError()
        //return messageChatDao.getChatWithMessages(chatId)
    }

    override suspend fun createChat(name: String) {
        val chat = MessageChatEntity(
            name = name,
        )
        //return messageChatDao.insertChat(chat)
    }

    override suspend fun addMessageToChat(chatId: Int, message: MessageGroup) {
        val chat = getChatById(chatId) ?: return
        val updatedMessages = chat.messages.plus(message)
        //TODO
    }

    override suspend fun updateChat(chat: MessageChat) {
        //messageChatDao.updateChat(chat.toEntity())
    }

    override suspend fun updateMessage(message: MessageGroup) {
        TODO()
    }

    override suspend fun updateSubMessage(subMessage: MessageAI) {
        TODO()
    }

    override suspend fun deleteChat(chatId: Int) {
        //messageChatDao.deleteChat(chatId)
    }

    override suspend fun clearAllChats() {
        //messageChatDao.clearAllChats()
    }
} 