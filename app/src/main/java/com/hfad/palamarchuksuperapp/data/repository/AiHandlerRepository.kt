package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.DataStoreHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandler
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.domain.usecases.GetModelsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AiHandlerRepository {
    val handlerList: List<AiModelHandler>
    suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError>
}

class AiHandlerRepositoryImpl @Inject constructor(
    //private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
    private val dataStoreHandler: DataStoreHandler,
    private val getModelsUseCase: GetModelsUseCase,
) : AiHandlerRepository {

    override val handlerList: List<AiModelHandler> =
        dataStoreHandler.aiHandlerList.data.map { prefernces ->
            prefernces["enabled"] ?: listOf(
                AiHandler(
                    modelName = AiProviderName.GEMINI,
                    chosen = true,
                    enabled = true,
                    model = AiModel.GEMINI_BASE_MODEL,
                )
            )
        }.first()
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