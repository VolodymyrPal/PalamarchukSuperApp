package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.repository.AiHandlerRepository
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface GetAiHandlersUseCase {
    suspend operator fun invoke(): StateFlow<PersistentList<AiModelHandler>>
}

class GetAiHandlersUseCaseImpl @Inject constructor(
    private val aiHandlerRepository: AiHandlerRepository,
) : GetAiHandlersUseCase {
    override suspend operator fun invoke(): StateFlow<PersistentList<AiModelHandler>> =
        aiHandlerRepository.getHandlerFlow()
}