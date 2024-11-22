package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.PersistentList

interface AiModelHandler {

    val modelName : HandlerName
    val chosen : Boolean
    val enabled : Boolean
    val baseModel: AiModel

    suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
    ): Result<SubMessageAI, AppError>

    suspend fun getModels(
    ): Result<List<AiModel>, AppError>
}

enum class HandlerName {
    OPENAI,
    GEMINI,
    GROQ
}