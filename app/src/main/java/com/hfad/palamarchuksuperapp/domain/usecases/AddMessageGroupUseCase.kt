package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface AddMessageGroupUseCase {
    suspend operator fun invoke(message: MessageGroup): Result<Long, AppError>
}

class AddMessageGroupUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : AddMessageGroupUseCase {
    override suspend operator fun invoke(message: MessageGroup): Result<Long, AppError> {
        val result = chatController.addMessageGroup(message).getOrHandleAppError {
            return Result.Error(it)
        }
        return Result.Success(result)
    }
}