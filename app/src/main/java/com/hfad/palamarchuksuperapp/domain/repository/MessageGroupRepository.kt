package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.AppResult

interface MessageGroupRepository {
    suspend fun getMessageGroup(chatId: Int): AppResult<MessageGroup, AppError>
    suspend fun updateMessageGroupEntity(messageGroupEntity: MessageGroupEntity): AppResult<Unit, AppError>
    suspend fun addMessageGroupEntity(messageGroupEntity: MessageGroupEntity): AppResult<Long, AppError>
}