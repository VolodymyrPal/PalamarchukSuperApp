package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface GetAiChatUseCase {
    operator fun invoke(): MutableStateFlow<PersistentList<MessageGroup>>
}

class GetAiChatUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : GetAiChatUseCase {
    override operator fun invoke(): MutableStateFlow<PersistentList<MessageGroup>> {
        return chatAiRepository.chatAiChatFlow
    }
}