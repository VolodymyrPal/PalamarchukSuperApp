package com.hfad.palamarchuksuperapp.data.repository

import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import com.hfad.palamarchuksuperapp.domain.repository.MessageAiRepository
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import com.hfad.palamarchuksuperapp.domain.repository.MessageGroupRepository
import com.hfad.palamarchuksuperapp.domain.usecases.getOrHandleAppError
import javax.inject.Inject

class ChatControllerImpl @Inject constructor(
    private val messageAiRepository: MessageAiRepository,
    private val messageGroupRepository: MessageGroupRepository,
    private val messageChatRepository: MessageChatRepository,
) : ChatController,
    MessageChatRepository by messageChatRepository,
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

    @Transaction
    override suspend fun addChatWithMessages(chat: MessageChat): Result<Long, AppError> {
        val messageToInsert = MessageChatWithRelationsEntity.from(chat)
        val insertedChatId = insertMessages(messageToInsert).getOrHandleAppError {
            return Result.Error(it)
        }
        return Result.Success(insertedChatId)
    }

    @Transaction
    private suspend fun insertMessages(
        chat: MessageChatWithRelationsEntity,
    ): Result<Long, AppError> {
        val chatId = createChat(chat.chat).getOrHandleAppError {
            return Result.Error(it)
        }
        chat.messageGroupsWithMessageEntity.forEach { messageGroup ->
            val groupId = addMessageGroupEntity(messageGroup.group.copy(chatId = chatId.toInt()))
                .getOrHandleAppError {
                    return Result.Error(
                        it
                    )
                }
            for (messageAi in messageGroup.messages) {
                val insertedMessage = messageAi.copy(messageGroupId = groupId.toInt())
                addMessageAiEntity(insertedMessage)
            }
        }

        return Result.Success(chatId)
    }
}