package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import javax.inject.Inject

interface GetModelsUseCase {
    suspend operator fun invoke(llmName: LLMName): AppResult<List<AiModel>, AppError>
}

class GetModelsUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : GetModelsUseCase {

    override suspend fun invoke(llmName: LLMName): AppResult<List<AiModel>, AppError> {
        return aiHandlerRepository.getBaseModels(llmName)
    }

}