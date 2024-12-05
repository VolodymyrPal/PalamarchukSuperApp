package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface AiHandlerRepository {
    suspend fun getHandlerFlow(): StateFlow<PersistentList<AiModelHandler>>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
}

class AiHandlerRepositoryImpl @Inject constructor(
    private val dataStoreHandler: DataStoreHandler,
    private val getModelsUseCase: GetModelsUseCase,
) : AiHandlerRepository {

    override suspend fun getHandlerFlow(): StateFlow<PersistentList<AiModelHandler>> =
        _handlerFlow()

    suspend fun _handlerFlow(): MutableStateFlow<PersistentList<AiModelHandler>> {
        return MutableStateFlow(
            dataStoreHandler.getAiHandlerList().filter { it.aiHandlerInfo.isActive }
                .toPersistentList()
        )
    }
    /**
     * Describe how to get handlers and sort them
     */

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return getModelsUseCase(handler)
    }

}