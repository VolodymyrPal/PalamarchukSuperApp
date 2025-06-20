package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.core.domain.AppResult

interface MessageAiRepository {
    suspend fun addMessageAiEntity(messageAiEntity: MessageAiEntity): AppResult<Long, AppError>
    suspend fun addAndGetMessageAi(messageAI: MessageAI): AppResult<MessageAI, AppError>
    suspend fun updateMessageAi(messageAI: MessageAI): AppResult<Unit, AppError>
    suspend fun getAllMessagesWithStatus(
        chatId: Int,
        status: MessageStatus,
    ): AppResult<List<MessageAI>, AppError>
}