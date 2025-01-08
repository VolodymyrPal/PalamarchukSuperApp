package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface GetAiChatUseCase {
    suspend operator fun invoke(chatId: Int): MessageChat?
    operator fun invoke(): StateFlow<PersistentList<MessageGroup>>
}

class GetAiChatUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : GetAiChatUseCase {
    override operator fun invoke() = chatAiRepository.chatAiFlow

    override suspend operator fun invoke(chatId: Int): MessageChat? {
        return chatAiRepository.getChatById(chatId)
    }
}