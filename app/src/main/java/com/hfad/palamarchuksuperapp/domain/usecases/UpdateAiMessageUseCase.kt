package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(messageAI: MessageAI)
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : UpdateAiMessageUseCase {

    override suspend operator fun invoke(messageAI: MessageAI) {
        chatAiRepository.updateSubMessage(messageAI)
    }
}