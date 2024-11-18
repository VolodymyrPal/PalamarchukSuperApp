package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow

interface AiHandlerDispatcher {
    val handlerList: List<AiModelHandler>
    suspend fun getModelsFromHandler(handler: AiModelHandler): List<AiModel>
    suspend fun getResponse(message: MessageAI)
    fun observeChatFlow(): Flow<PersistentList<MessageAI>>
    fun observeErrors(): Flow<AppError?>
}