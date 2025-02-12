package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result

interface MessageGroupRepository {
    suspend fun getMessageGroup(chatId: Int): Result<MessageGroup, AppError>
    suspend fun updateMessageGroupEntity(messageGroupEntity: MessageGroupEntity): Result<Unit, AppError>
    suspend fun addMessageGroupEntity(messageGroupEntity: MessageGroupEntity): Result<Long, AppError>
}