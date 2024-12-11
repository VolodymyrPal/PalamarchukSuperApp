package com.hfad.palamarchuksuperapp.data.repository

import android.util.Log
import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface AiHandlerRepository {
    suspend fun getHandlerFlow(): StateFlow<PersistentList<AiModelHandler>>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
    suspend fun addHandler(handler: AiModelHandler)
    suspend fun removeHandler(handler: AiModelHandler)
    suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo)
}

class AiHandlerRepositoryImpl @Inject constructor(
    private val dataStoreHandler: DataStoreHandler,
    private val getModelsUseCase: GetModelsUseCase,
) : AiHandlerRepository {

    override suspend fun getHandlerFlow(): StateFlow<PersistentList<AiModelHandler>> =
        _handlerFlow()

    private suspend fun _handlerFlow(): MutableStateFlow<PersistentList<AiModelHandler>> {
        return MutableStateFlow(
            dataStoreHandler.getAiHandlerList().toPersistentList()
        )
    }

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return getModelsUseCase(handler)
    }

    override suspend fun addHandler(handler: AiModelHandler) {
        _handlerFlow().update {
            it.add(handler)
        }
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)
    }

    override suspend fun removeHandler(handler: AiModelHandler) {
        _handlerFlow().update {
            it.remove(handler)
        }
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)

    }

    override suspend fun updateHandler(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        Log.d("AiHandler Repository", "updateHandler: $aiHandlerInfo")
        handler.setAiHandlerInfo(
            aiHandlerInfo
        )
        dataStoreHandler.saveAiHandlerList(_handlerFlow().value)
    }

}