package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.MessageAiDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageAiRepository
import javax.inject.Inject

class MessageAiRepositoryImpl @Inject constructor(
    private val messageAiDao: MessageAiDao,
) : MessageAiRepository {

    override suspend fun addMessageAiEntity(messageAiEntity: MessageAiEntity): Result<Long, AppError> {
        return withSqlErrorHandling {
            messageAiDao.insertMessage(messageAiEntity)
        }
    }

    override suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError> {
        return withSqlErrorHandling {
            val messageAiEntity =
                messageAiDao.insertAndReturnMessage(MessageAiEntity.from(messageAI))
            MessageAI.from(messageAiEntity)
        }
    }

    override suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageAiDao.updateMessage(
                MessageAiEntity.from(messageAI)
            )
        }
    }

    override suspend fun getAllMessagesWithStatus(
        chatId: Int,
        status: MessageStatus,
    ): Result<List<MessageAI>, AppError> {
        return withSqlErrorHandling {
            messageAiDao.getMessagesWithStatus(chatId, status.name)
                .map { MessageAI.from(it) }
        }
    }
}