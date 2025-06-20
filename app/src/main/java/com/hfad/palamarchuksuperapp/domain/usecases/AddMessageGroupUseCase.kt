package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface AddMessageGroupUseCase {
    suspend operator fun invoke(message: MessageGroup): AppResult<Long, AppError>
}

class AddMessageGroupUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : AddMessageGroupUseCase {
    override suspend operator fun invoke(message: MessageGroup): AppResult<Long, AppError> {
        val result = chatController.addMessageGroup(message).getOrHandleAppError {
            return AppResult.Error(it)
        }
        return AppResult.Success(result)
    }
}