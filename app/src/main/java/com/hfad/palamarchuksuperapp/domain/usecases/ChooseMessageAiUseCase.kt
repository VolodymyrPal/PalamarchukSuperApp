package com.hfad.palamarchuksuperapp.domain.usecases

import kotlinx.collections.immutable.toPersistentList
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAiIndex: Int, subMessageAiIndex: Int)
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val getAiChatUseCase: GetAiChatUseCase,
    private val updateMessageUseCase: UpdateAiMessageUseCase,
) : ChooseMessageAiUseCase {
    override suspend operator fun invoke(messageAiIndex: Int, subMessageAiIndex: Int) {
        val currentList = getAiChatUseCase().value.toMutableList().also { list ->
            list[messageAiIndex] = list[messageAiIndex].copy(
                content = getAiChatUseCase().value[messageAiIndex].content.map {
                    if (it.id == subMessageAiIndex) {
                        it.copy(isChosen = true)
                    } else it.copy(
                        isChosen = false
                    )
                }.toPersistentList()
            )
        }
        updateMessageUseCase(currentList.toPersistentList())
    }
}