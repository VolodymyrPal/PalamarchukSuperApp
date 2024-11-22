package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface ChangeAiMessageUseCase {
    suspend operator fun invoke(list: PersistentList<MessageAI>)
    suspend operator fun invoke(listSubMessageAI: PersistentList<SubMessageAI>, messageIndex: Int)
}

class ChangeAiMessageUseCaseImpl @Inject constructor(
    private val getAiChatUseCase: GetAiChatUseCase
) : ChangeAiMessageUseCase {
    override suspend operator fun invoke(list: PersistentList<MessageAI>) {
        getAiChatUseCase().update {
            list
        }
    }

    override suspend operator fun invoke(listSubMessageAI: PersistentList<SubMessageAI>, messageIndex: Int) {
        getAiChatUseCase().update {
            it.set(messageIndex, it[messageIndex].copy(content = listSubMessageAI))
        }
    }
}