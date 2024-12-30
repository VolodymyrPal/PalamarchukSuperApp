package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow

interface AiModelHandler {
    val aiHandlerInfo: StateFlow<AiHandlerInfo>

    suspend fun getResponse(
        messageList: PersistentList<MessageGroup>,
        messageAiID: Int
    ): Result<SubMessageAI, AppError>

    suspend fun getModels(
    ): Result<List<AiModel>, AppError>

    fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo)
}