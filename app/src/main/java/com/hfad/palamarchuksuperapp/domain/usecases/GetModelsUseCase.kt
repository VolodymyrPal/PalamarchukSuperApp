package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface GetModelsUseCase {
    suspend operator fun invoke(handler: AiModelHandler): Result<List<AiModel>, AppError>
    suspend operator fun invoke(llmName: LLMName): Result<List<AiModel>, AppError>
}

class GetModelsUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : GetModelsUseCase {
    override suspend operator fun invoke(handler: AiModelHandler): Result<List<AiModel>, AppError> {
        return handler.getModels()
    }

    override suspend fun invoke(llmName: LLMName): Result<List<AiModel>, AppError> {
        return aiHandlerRepository.getBaseModels(llmName)
    }

}