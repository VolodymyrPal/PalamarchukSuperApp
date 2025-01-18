package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveAiHandlersUseCase {
    operator fun invoke(): Flow<List<AiModelHandler>>
}

class ObserveAiHandlersUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : ObserveAiHandlersUseCase {

    override operator fun invoke(): Flow<List<AiModelHandler>> =
        aiHandlerRepository.aiHandlerFlow
}