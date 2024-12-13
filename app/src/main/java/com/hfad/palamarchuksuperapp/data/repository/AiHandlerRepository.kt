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
        _handlerFlow().update { currentList ->
            val newList = currentList.add(newHandler)
            newList
        }
        Log.d("Handler", _handlerFlow().value.toString())
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)
    }

    override suspend fun removeHandler(handler: AiModelHandler) {
        _handlerFlow().update {
            it.remove(handler)
        }
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)

    }

    override suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        handler.setAiHandlerInfo(
            aiHandlerInfo
        )
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)
    }

}