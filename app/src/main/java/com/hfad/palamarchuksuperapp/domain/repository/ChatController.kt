package com.hfad.palamarchuksuperapp.domain.repository

import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.usecases.getOrHandleAppError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ChatController {
    suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError>
    suspend fun getAllMessagesWithStatus(
        chatId: Int, status: MessageStatus,
    ): Result<List<MessageAI>, AppError>

    suspend fun getAllChats(): Result<List<MessageChat>, AppError>
    suspend fun getAllChatsInfo(): Result<Flow<List<MessageChat>>, AppError>
    suspend fun getChatWithMessagesById(chatId: Int): Result<MessageChat, AppError>
    suspend fun getChatFlowById(chatId: Int): Result<Flow<MessageChat>, AppError>
    suspend fun createChat(emptyChat: MessageChat): Result<Unit, AppError>
    suspend fun addAndGetChat(chat: MessageChat): Result<MessageChat, AppError>
    suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError>
    suspend fun deleteChat(chatId: Int): Result<Unit, AppError>
    suspend fun clearAllChats(): Result<Unit, AppError>
    suspend fun isChatExist(chatId: Int): Result<Boolean, AppError>
    suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError>
    suspend fun addMessageGroup(messageGroupWithChatID: MessageGroup): Result<Long, AppError>
    suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError>
}

class MessageChatControllerImpl @Inject constructor(
    private val messageAiRepository: MessageAiRepository,
    private val messageGroupRepository: MessageGroupRepository,
    private val messageChatRepository: MessageChatRepository,
) : ChatController, MessageChatRepository by messageChatRepository,
    MessageAiRepository by messageAiRepository,
    MessageGroupRepository by messageGroupRepository {

    /**
     * Вставка группы сообщений со всеми её сообщениями.
     * @param groupWithMessages Группа сообщений со связанными сообщениями
     * @return ID вставленной группы
     */
    @Transaction
    override suspend fun addMessageGroup(
        messageGroupWithChatID: MessageGroup,
    ): Result<Long, AppError> {    //TODO NEARLY DONE

        val groupId = addMessageGroupEntity(
            MessageGroupEntity.from(messageGroupWithChatID)
        ).getOrHandleAppError {
            return Result.Error(it)
        }

        messageGroupWithChatID.content.forEach { messageAi ->
            messageAiRepository.addMessageAiEntity(
                MessageAiEntity.from(messageAi.copy(messageGroupId = groupId.toInt()))
            ).getOrHandleAppError {
                return Result.Error(it)
            }
        }

        return Result.Success(groupId)
    }

    @Transaction
    override suspend fun updateMessageGroup(messageGroup: MessageGroup): Result<Unit, AppError> {
        return withSqlErrorHandling {
            updateMessageGroupEntity(MessageGroupEntity.from(messageGroup))
            messageGroup.content.forEach { messageAi ->
                val insertedMessage = messageAi.copy(messageGroupId = messageGroup.id)
                messageAiRepository.addMessageAiEntity(MessageAiEntity.from(insertedMessage))
            }
        }
    }


//    @Transaction
//    suspend fun insertAndReturnChat(chat: MessageChatWithRelationsEntity): MessageChatWithRelationsEntity {
//        val chatId = insertChat(chat.chat) // Вставляем объект и получаем его ID
//        chat.messageGroupsWithMessageEntity.forEach { messageGroup ->
//            val groupId =
//                messageGroupDao.insertMessageGroup(messageGroup.group.copy(chatId = chatId.toInt()))
//            for (messageAi in messageGroup.messages) {
//                val insertedMessage = messageAi.copy(messageGroupId = groupId.toInt())
//                messageAiDao.insertMessage(insertedMessage)
//            }
//        }
//        return messageChatDao.getChatWithMessages(chatId.toInt()) // Достаём объект по ID
//    }

}