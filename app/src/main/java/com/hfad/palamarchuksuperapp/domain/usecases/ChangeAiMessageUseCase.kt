package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface ChangeAiMessageUseCase {
    suspend operator fun invoke(list: PersistentList<MessageAI>, messageIndex: Int)
}

class ChangeAiMessageUseCaseImpl @Inject constructor(
    private val getAiChatUseCase: GetAiChatUseCase
) : ChangeAiMessageUseCase {
    override suspend operator fun invoke(list: PersistentList<MessageAI>, messageIndex: Int) {
        getAiChatUseCase().update {
            list
        }
    }
}