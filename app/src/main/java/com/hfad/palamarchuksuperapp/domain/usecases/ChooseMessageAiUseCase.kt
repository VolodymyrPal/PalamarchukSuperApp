package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAI: MessageAI)
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
    private val updateMessageUseCase: UpdateAiMessageUseCase,
) : ChooseMessageAiUseCase {

    override suspend operator fun invoke(messageAI: MessageAI) {
        val group = chatAiRepository.getMessageGroupById(messageAI.messageGroupId).toDomainModel()

        // Обновляем все сообщения в группе, устанавливая isChosen
        group.content.forEach { message ->
            updateMessageUseCase(message.copy(isChosen = message.id == messageAI.id))
        }
    }
}