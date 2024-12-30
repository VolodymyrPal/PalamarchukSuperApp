package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(list: PersistentList<MessageGroup>)
    suspend operator fun invoke(listSubMessageAI: PersistentList<SubMessageAI>, messageAiIndex: Int)
    suspend operator fun invoke(message: MessageGroup, messageIndex: Int)
    suspend operator fun invoke(subMessageAI: SubMessageAI, messageIndex: Int, subMessageIndex: Int)
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : UpdateAiMessageUseCase {
    override suspend operator fun invoke(list: PersistentList<MessageGroup>) {
        chatAiRepository.updateChat(list)
    }

    override suspend operator fun invoke(
        listSubMessageAI: PersistentList<SubMessageAI>,
        messageAiIndex: Int,
    ) {
        chatAiRepository.updateSubMessages(messageAiIndex, listSubMessageAI)
    }

    override suspend fun invoke(message: MessageGroup, messageIndex: Int) {
        chatAiRepository.updateMessage(messageIndex, message)
    }

    override suspend fun invoke(
        subMessageAI: SubMessageAI,
        messageIndex: Int,
        subMessageIndex: Int
    ) {
        chatAiRepository.updateSubMessage(
            messageIndex,
            subMessageAI,
            indexSubMessage = subMessageIndex
        )
    }
}