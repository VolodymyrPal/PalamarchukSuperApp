package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface AddMessageGroupUseCase {
    suspend operator fun invoke(message: MessageGroup)
}

class AddMessageGroupUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : AddMessageGroupUseCase {
    override suspend operator fun invoke(message: MessageGroup) {
        chatAiRepository.addMessageGroup(message)
    }
}