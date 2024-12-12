package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface UpdateAiHandlerUseCase {
    suspend operator fun invoke(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo)
}

class UpdateAiHandlerUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : UpdateAiHandlerUseCase {
    override suspend operator fun invoke(handler: AiModelHandler, aiHandlerInfo: AiHandlerInfo) {
        aiHandlerRepository.updateHandler(
            handler = handler,
            aiHandlerInfo = aiHandlerInfo
        )
    }
}