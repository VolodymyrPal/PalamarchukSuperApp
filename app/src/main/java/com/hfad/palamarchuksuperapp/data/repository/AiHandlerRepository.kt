package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface AiHandlerRepository {
    val aiHandlerFlow: Flow<List<AiModelHandler>>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
    suspend fun addHandler(handlerInfo: AiHandlerInfo)
    suspend fun removeHandler(handler: AiModelHandler)
    suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo)
}

class AiHandlerRepositoryImpl @Inject constructor(
    private val dataStoreHandler: DataStoreHandler,
    private val getModelsUseCase: GetModelsUseCase,
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
        return getModelsUseCase(handler)
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
        val jsonToSave = Json.encodeToString(aiHandlerFlow.first().map { it.aiHandlerInfo })
        dataStoreHandler.saveAiHandlerList(jsonToSave)
    }

}