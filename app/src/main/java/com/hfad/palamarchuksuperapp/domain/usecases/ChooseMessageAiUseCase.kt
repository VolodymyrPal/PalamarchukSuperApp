package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError>
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val chatController: ChatController
) : ChooseMessageAiUseCase {

    override suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError> {
        val groupWithMessages =
            chatController.getMessageGroup(messageAI.messageGroupId).getOrHandleAppError {
                return Result.Error(it)
            }

        groupWithMessages.content.forEach { message ->
            chatController.updateMessageAi(
                message.copy(
                    isChosen = message.id == messageAI.id,
                )
            ).getOrHandleAppError {
                return Result.Error(it)
            }
        }
        return Result.Success(Unit)
    }
}