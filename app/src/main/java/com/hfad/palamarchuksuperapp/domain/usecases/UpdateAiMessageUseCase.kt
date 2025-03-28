package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError>
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : UpdateAiMessageUseCase {

    override suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError> {
        val updatedMessage = chatController.updateMessageAi(messageAI).getOrHandleAppError {
            return Result.Error(it)
        }
        return Result.Success(updatedMessage)
    }
}