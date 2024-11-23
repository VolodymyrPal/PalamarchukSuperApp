package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(list: PersistentList<MessageAI>)
    suspend operator fun invoke(listSubMessageAI: PersistentList<SubMessageAI>, messageIndex: Int)
    suspend operator fun invoke(message: MessageAI, messageIndex: Int)
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : UpdateAiMessageUseCase {
    override suspend operator fun invoke(list: PersistentList<MessageAI>) {
        chatAiRepository.updateChat(list)
    }

    override suspend operator fun invoke(
        listSubMessageAI: PersistentList<SubMessageAI>,
        messageIndex: Int,
    ) {
        chatAiRepository.updateSubMessage(messageIndex, listSubMessageAI)
    }

    override suspend fun invoke(message: MessageAI, messageIndex: Int) {
        chatAiRepository.updateMessage(messageIndex, message)
    }
}