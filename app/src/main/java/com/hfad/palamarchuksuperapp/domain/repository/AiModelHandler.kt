package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.repository.AiModels
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.PersistentList

interface AiModelHandler {
    suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels? = null,
    ): Result<MessageAI, AppError>

    suspend fun getModels(
    ): Result<List<AiModels>, AppError>
}