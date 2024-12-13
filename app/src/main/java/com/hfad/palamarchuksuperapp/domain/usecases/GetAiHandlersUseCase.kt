package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetAiHandlersUseCase {
    operator fun invoke(): Flow<List<AiModelHandler>>
}

class GetAiHandlersUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : GetAiHandlersUseCase {

    override operator fun invoke(): Flow<List<AiModelHandler>> =
        aiHandlerRepository.aiHandlerFlow
}