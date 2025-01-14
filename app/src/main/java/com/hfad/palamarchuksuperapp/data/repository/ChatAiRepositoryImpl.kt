package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class ChatAiRepositoryImpl @Inject constructor() : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageGroup>> =
        MutableStateFlow(MockChat())

    override val errorFlow: MutableSharedFlow<AppError?> = MutableSharedFlow()

    override suspend fun getModels(handler: AiModelHandler): List<AiModel> {
        val result = handler.getModels()
        return if (result is Result.Success) {
            result.data
        } else {
            errorFlow.emit(AppError.CustomError((result as Result.Error).error.toString()))
            listOf()
        }
    }

    override suspend fun getAllChats(): List<MessageChat> {
        return messageChatDao.getAllChatsWithMessages().map { it.toDomainModel() }
    }

    override suspend fun getChatWithMessagesById(chatId: Int): MessageChat {
        if (messageChatDao.getAllChatsInfo().find { it.id == chatId } == null) {
            Log.d("ChatAiRepositoryImpl", "getChatById: $chatId")
            val id = messageChatDao.insertChat(MessageChatEntity(name = "Base chat"))
            return messageChatDao.getChatWithMessages(id.toInt()).toDomainModel()
        } else {
            return messageChatDao.getChatWithMessages(chatId)
                .toDomainModel() //Could produce potential error
        }
    }

    override suspend fun getChatFlowById(chatId: Int): Flow<MessageChat> {
        return if (messageChatDao.getAllChatsInfo().find { it.id == chatId } == null) {
            val id = messageChatDao.insertChatWithRelations(
                MessageChatWithRelationsEntity(
                    chat = MessageChatEntity(name = "Base chat"),
                    messages = MockChat().map { it.toEntityWithRelations() } //TODO For testing
                )
            )
            messageChatDao.getChatWithMessagesFlow(id.toInt())
                .map { value -> value.toDomainModel() }
        } else {
            messageChatDao.getChatWithMessagesFlow(chatId).map { value ->
                value.toDomainModel()
            }
        }
    }

    override suspend fun getMessageGroupById(chatId: Int): MessageGroupWithMessagesEntity {
        return messageChatDao.getMessageGroup(chatId)
    }

    override suspend fun createChat(chat: MessageChat) {
        messageChatDao.insertChat(
            MessageChatEntity(
                name = chat.name,
                timestamp = chat.timestamp
            )
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
            messageGroupWithChatID.toEntityWithRelations()
        )
        return messageGroupId
    }

    override suspend fun updateMessageGroup(messageGroup: MessageGroup) {
        messageChatDao.updateMessageGroupWithContent(
            messageGroup.toEntityWithRelations()
        )
    }

}