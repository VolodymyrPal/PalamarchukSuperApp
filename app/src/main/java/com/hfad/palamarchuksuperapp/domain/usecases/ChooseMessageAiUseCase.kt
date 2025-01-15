package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAI: MessageAI)
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : ChooseMessageAiUseCase {

    override suspend operator fun invoke(messageAI: MessageAI) {
        val group = MessageGroupWithMessagesEntity.toDomainModel(
            chatAiRepository.getMessageGroupWithMessagesById(messageAI.messageGroupId)
        )
        // Обновляем все сообщения в группе, устанавливая isChosen
        group.content.forEach { message ->
            chatAiRepository.updateMessageAi(
                message.copy(
                    isChosen = message.id == messageAI.id,
                )
            )
        }
    }
}