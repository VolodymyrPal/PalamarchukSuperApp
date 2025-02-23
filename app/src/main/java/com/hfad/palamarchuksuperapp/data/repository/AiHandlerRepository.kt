package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface AiHandlerRepository {
    val aiHandlerFlow: Flow<List<AiModelHandler>>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
    suspend fun addHandler(handlerInfo: AiHandlerInfo)
    suspend fun removeHandler(handler: AiModelHandler)
    suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo)
    suspend fun getBaseModels(llmName: LLMName): Result<List<AiModel>, AppError>
}

class AiHandlerRepositoryImpl @Inject constructor(
    private val dataStoreHandler: DataStoreHandler,
    private val mapAiModelHandlerUseCase: MapAiModelHandlerUseCase,
) : AiHandlerRepository {

    override val aiHandlerFlow: Flow<List<AiModelHandler>> = dataStoreHandler.getAiHandlerList.map {
        if (it.isNotBlank()) {
            mapAiModelHandlerUseCase(
                Json.decodeFromString<List<AiHandlerInfo>>(
                    it
                )
            )
        } else {
            mapAiModelHandlerUseCase(AiHandlerInfo.DEFAULT_LIST_AI_HANDLER_INFO)
        }
    }

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return handler.getModels()
    }

    override suspend fun addHandler(handlerInfo: AiHandlerInfo) {
        val newHandler = mapAiModelHandlerUseCase(handlerInfo)
        val newList = aiHandlerFlow.first().toMutableList().apply {
            add(newHandler)
        }
        val jsonToSave = Json.encodeToString(newList.map { it.aiHandlerInfo.value })
        dataStoreHandler.saveAiHandlerList(jsonToSave)
    }

    override suspend fun removeHandler(handler: AiModelHandler) {
        val list = aiHandlerFlow.first().toMutableList().also { handlerList ->
            handlerList.removeIf { it.aiHandlerInfo.value.id == handler.aiHandlerInfo.value.id }
        }
        val jsonToSave = Json.encodeToString(list.map { it.aiHandlerInfo.value })
        dataStoreHandler.saveAiHandlerList(jsonToSave)
    }

    override suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        handler.setAiHandlerInfo(
            aiHandlerInfo
        )

        aiHandlerFlow.take(1).collect {
            val updatedHandlers = it.map {
                if (it.aiHandlerInfo.value.id == handler.aiHandlerInfo.value.id) {
                    handler.aiHandlerInfo.value
                } else {
                    it.aiHandlerInfo.value
                }
            }
            val jsonToSave = Json.encodeToString(updatedHandlers)
            dataStoreHandler.saveAiHandlerList(jsonToSave)
        }
    }

    private val mutableBaseHandlerMap: MutableMap<LLMName, AiModelHandler> by lazy { mutableMapOf() }

    override suspend fun getBaseModels(llmName: LLMName): Result<List<AiModel>, AppError> {
        val baseHandler = mutableBaseHandlerMap.getOrPut(llmName) {
            when (llmName) {
                LLMName.OPENAI -> mapAiModelHandlerUseCase(AiHandlerInfo.DEFAULT_AI_HANDLER_INFO_OPEN_AI)
                LLMName.GEMINI -> mapAiModelHandlerUseCase(AiHandlerInfo.DEFAULT_AI_HANDLER_INFO_GEMINI)
                LLMName.GROQ -> mapAiModelHandlerUseCase(AiHandlerInfo.DEFAULT_AI_HANDLER_INFO_GROQ)
            }
        }
        return baseHandler.getModels()
    }
}