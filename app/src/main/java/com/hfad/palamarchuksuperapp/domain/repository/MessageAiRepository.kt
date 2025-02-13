package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.Result

interface MessageAiRepository {
    suspend fun addMessageAiEntity(messageAiEntity: MessageAiEntity): Result<Long, AppError>
    suspend fun addAndGetMessageAi(messageAI: MessageAI): Result<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): Result<Unit, AppError>
    suspend fun getAllMessagesWithStatus(
        chatId: Int,
        status: MessageStatus,
    ): Result<List<MessageAI>, AppError>
}