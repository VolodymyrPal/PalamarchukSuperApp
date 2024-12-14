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

    override val aiHandlerFlow: Flow<List<AiModelHandler>> = dataStoreHandler.getAiHandlerList

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return getModelsUseCase(handler)
    }

    override suspend fun addHandler(handlerInfo: AiHandlerInfo) {
        val newHandler = mapAiModelHandlerUseCase(handlerInfo)
        val newList = aiHandlerFlow.first().toMutableList().apply {
            add(newHandler)
        }
        dataStoreHandler.saveAiHandlerList(newList)
    }

    override suspend fun removeHandler(handler: AiModelHandler) {
        val list = aiHandlerFlow.first().toMutableList().also { handlerList ->
            handlerList.removeIf { it.aiHandlerInfo.value.id == handler.aiHandlerInfo.value.id }
        }
        dataStoreHandler.saveAiHandlerList(list)
    }

    override suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        handler.setAiHandlerInfo(
            aiHandlerInfo
        )
        dataStoreHandler.saveAiHandlerList(aiHandlerFlow.first())
    }

}