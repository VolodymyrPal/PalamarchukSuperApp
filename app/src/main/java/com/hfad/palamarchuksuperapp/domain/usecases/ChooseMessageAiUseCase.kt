package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke (subMessageAI: SubMessageAI, indexOfMessage: Int)
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val getAiChatUseCase: GetAiChatUseCase,
    private val updateMessageUseCase: UpdateAiMessageUseCase
) : ChooseMessageAiUseCase {
    override suspend operator fun invoke(subMessageAI: SubMessageAI, indexOfMessage: Int) {
        val list = getAiChatUseCase().first()
        val message = list[indexOfMessage]
        val listSubMessageAI = message.content.toMutableList()
        val chosenMessage = listSubMessageAI.map {subMessage ->
            if (subMessage == subMessageAI) {
                subMessage.copy(isChosen = true)
            } else {
                subMessage.copy(isChosen = false)
            }
        }.toPersistentList()

        updateMessageUseCase(chosenMessage, indexOfMessage)
    }
}