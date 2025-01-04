package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageAI

interface MessageChatRepository { //TODO
    fun getAllChats(): List<MessageChat>
    suspend fun getChatById(chatId: Int): MessageChat?
    suspend fun createChat(name: String)
    suspend fun addMessageToChat(chatId: Int, message: MessageGroup)
    suspend fun updateChat(chat: MessageChat)
    suspend fun updateMessage(message: MessageGroup)
    suspend fun updateSubMessage(subMessage: MessageAI)
    suspend fun deleteChat(chatId: Int)
    suspend fun clearAllChats()
} 