package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.core.data.withSqlErrorHandling
import com.hfad.palamarchuksuperapp.data.dao.MessageGroupDao
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageGroupRepository
import javax.inject.Inject

class MessageGroupRepositoryImpl @Inject constructor(
    private val messageGroupDao: MessageGroupDao,
) : MessageGroupRepository {

    override suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError> {
        return withSqlErrorHandling {
            MessageGroup.from(
                messageGroupDao.getMessageGroup(chatId)
            )
        }
    }

    override suspend fun addMessageGroupEntity(messageGroupEntity: MessageGroupEntity): Result<Long, AppError> {
        return withSqlErrorHandling {
            messageGroupDao.insertMessageGroup(messageGroupEntity)
        }
    }

    override suspend fun updateMessageGroupEntity(messageGroupEntity: MessageGroupEntity): Result<Unit, AppError> {
        return withSqlErrorHandling {
            messageGroupDao.updateMessageGroup(messageGroupEntity)
        }
    }

}