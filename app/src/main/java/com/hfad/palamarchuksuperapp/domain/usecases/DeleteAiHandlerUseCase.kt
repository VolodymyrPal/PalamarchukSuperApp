package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface DeleteAiHandlerUseCase {
    suspend operator fun invoke(handler: AiModelHandler)
}

class DeleteAiHandlerUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : DeleteAiHandlerUseCase {

    override suspend operator fun invoke(handler: AiModelHandler) {
        aiHandlerRepository.removeHandler(handler)
    }

}