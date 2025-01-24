package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface AddMessageGroupUseCase {
    suspend operator fun invoke(message: MessageGroup): Result<Long, AppError>
}

class AddMessageGroupUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : AddMessageGroupUseCase {
    override suspend operator fun invoke(message: MessageGroup): Result<Long, AppError> {
        val result = chatAiRepository.addMessageGroup(message).onSuccessOrReturnError {
            return Result.Error(it)
        }
        return Result.Success(result)
    }
}