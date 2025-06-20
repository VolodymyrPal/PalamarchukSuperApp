package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow

interface AiModelHandler {
    val aiHandlerInfo: StateFlow<AiHandlerInfo>

    suspend fun getResponse(
        messageList: PersistentList<MessageGroup>,
    ): AppResult<MessageAI, AppError> // Return MessageAI without info about messageGroupId

    suspend fun getModels(
    ): AppResult<List<AiModel>, AppError>

    fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo)
}