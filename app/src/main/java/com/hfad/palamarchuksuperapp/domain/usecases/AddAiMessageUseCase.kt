package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface AddAiMessageUseCase {
    suspend operator fun invoke(message: MessageAI)
}

class AddAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : AddAiMessageUseCase {
    override suspend operator fun invoke(message: MessageAI) {
        chatAiRepository.chatAiChatFlow.update { it.add(message) }
    }
}