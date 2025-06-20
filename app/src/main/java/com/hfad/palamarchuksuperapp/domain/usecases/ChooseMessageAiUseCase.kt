package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAI: MessageAI): AppResult<Unit, AppError>
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val chatController: ChatController
) : ChooseMessageAiUseCase {

    override suspend operator fun invoke(messageAI: MessageAI): AppResult<Unit, AppError> {
        val groupWithMessages =
            chatController.getMessageGroup(messageAI.messageGroupId).getOrHandleAppError {
                return AppResult.Error(it)
            }

        groupWithMessages.content.forEach { message ->
            chatController.updateMessageAi(
                message.copy(
                    isChosen = message.id == messageAI.id,
                )
            ).getOrHandleAppError {
                return AppResult.Error(it)
            }
        }
        return AppResult.Success(Unit)
    }
}