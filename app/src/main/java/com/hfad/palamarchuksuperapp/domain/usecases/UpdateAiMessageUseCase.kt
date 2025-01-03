package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(list: PersistentList<MessageGroup>)
    suspend operator fun invoke(listMessageAI: PersistentList<MessageAI>, messageAiIndex: Int)
    suspend operator fun invoke(message: MessageGroup, messageIndex: Int)
    suspend operator fun invoke(messageAI: MessageAI, messageIndex: Int, subMessageIndex: Int)
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : UpdateAiMessageUseCase {
    override suspend operator fun invoke(list: PersistentList<MessageGroup>) {
        chatAiRepository.updateChat(list)
    }

    override suspend operator fun invoke(
        listMessageAI: PersistentList<MessageAI>,
        messageAiIndex: Int,
    ) {
        chatAiRepository.updateSubMessages(messageAiIndex, listMessageAI)
    }

    override suspend fun invoke(message: MessageGroup, messageIndex: Int) {
        chatAiRepository.updateMessage(messageIndex, message)
    }

    override suspend fun invoke(
        messageAI: MessageAI,
        messageIndex: Int,
        subMessageIndex: Int
    ) {
        chatAiRepository.updateSubMessage(
            messageIndex,
            messageAI,
            indexSubMessage = subMessageIndex
        )
    }
}