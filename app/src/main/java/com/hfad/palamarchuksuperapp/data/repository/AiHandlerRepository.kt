package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import com.hfad.palamarchuksuperapp.domain.usecases.MapAiModelHandlerUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface AiHandlerRepository {
    suspend fun getHandlerFlow(): MutableStateFlow<PersistentList<AiModelHandler>>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
}

class AiHandlerRepositoryImpl @Inject constructor(
    private val dataStoreHandler: DataStoreHandler,
    private val getModelsUseCase: GetModelsUseCase,
) : AiHandlerRepository {

    override suspend fun getHandlerFlow(): MutableStateFlow<PersistentList<AiModelHandler>> =
        MutableStateFlow(
            dataStoreHandler.getAiHandlerList().filter { it.aiHandler.enabled }.toPersistentList()
        )
    //apiHandlers.filter { it.enabled }.sortedBy { it.modelName }

    /**
     * Describe how to get handlers and sort them
     */
    //MutableStateFlow(listOf(OpenAIApiHandler(HttpClient()))).value
    //handlerSettingStore.aiHandler TODO

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return getModelsUseCase(handler)
    }

}