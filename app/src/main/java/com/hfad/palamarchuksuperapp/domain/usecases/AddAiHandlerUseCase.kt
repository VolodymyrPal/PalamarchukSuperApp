package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import javax.inject.Inject

interface AddAiHandlerUseCase {
    suspend operator fun invoke(handler: AiHandlerInfo)
}

class AddAiHandlerUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : AddAiHandlerUseCase {

    override suspend fun invoke(handler: AiHandlerInfo) {
        aiHandlerRepository.addHandler(handler)
    }
}