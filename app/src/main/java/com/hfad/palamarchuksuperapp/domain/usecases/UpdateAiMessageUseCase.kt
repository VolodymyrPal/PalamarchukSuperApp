package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageAiRepository
import javax.inject.Inject

interface UpdateAiMessageUseCase {
    suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError>
}

class UpdateAiMessageUseCaseImpl @Inject constructor(
    private val messageAiRepository: MessageAiRepository,
) : UpdateAiMessageUseCase {

    override suspend operator fun invoke(messageAI: MessageAI): Result<Unit, AppError> {
        val updatedMessage = messageAiRepository.updateMessageAi(messageAI).getOrHandleAppError {
            return Result.Error(it)
        }
        return Result.Success(updatedMessage)
    }
}