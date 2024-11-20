package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

class AiHandlerRepositoryImpl @Inject constructor(
    private val apiHandlers: Set<@JvmSuppressWildcards AiModelHandler>,
    private val getModelsUseCase: GetModelsUseCase
) : AiHandlerRepository {

    override val handlerList: List<AiModelHandler> =
        apiHandlers.filter { it.enabled }.sortedBy { it.modelName }

    /**
     * Describe how to get handlers and sort them
     */
    //MutableStateFlow(listOf(OpenAIApiHandler(HttpClient()))).value
    //handlerSettingStore.aiHandler TODO

    override suspend fun getModelsFromHandler(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return getModelsUseCase(handler)
    }

}