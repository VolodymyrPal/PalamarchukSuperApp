package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface AddAiMessageUseCase {
    suspend operator fun invoke(chatId: Int, message: MessageGroup)
}

class AddAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : AddAiMessageUseCase {
    override suspend operator fun invoke(chatId: Int, message: MessageGroup) {
        chatAiRepository.addMessageGroup(chatId, message)
    }
}