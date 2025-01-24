package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface ChooseMessageAiUseCase {
    suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError>
}

class ChooseMessageAiUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : ChooseMessageAiUseCase {

    override suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError> {
        val groupWithMessages =
            chatAiRepository.getMessageGroup(messageAI.messageGroupId).onSuccessOrReturnError {
                return Result.Error(it)
            }

        groupWithMessages.content.forEach { message ->
            chatAiRepository.updateMessageAi(
                message.copy(
                    isChosen = message.id == messageAI.id,
                )
            ).onSuccessOrReturnError {
                return Result.Error(it)
            }
        }
        return Result.Success(Unit)
    }
}