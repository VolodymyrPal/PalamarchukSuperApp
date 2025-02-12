package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageAiRepository
import javax.inject.Inject

interface UpdateMessageStatusUseCase {
    suspend operator fun invoke (chatId: Int, messageStatus: MessageStatus) : Result<Unit, AppError>
}

class UpdateMessageStatusUseCaseImpl @Inject constructor (
    private val messageAiRepository: MessageAiRepository,
): UpdateMessageStatusUseCase {
    override suspend fun invoke(chatId: Int, messageStatus: MessageStatus) : Result<Unit, AppError> {
        val loadingMessages =
            messageAiRepository.getAllMessagesWithStatus(chatId, MessageStatus.LOADING)
                .getOrHandleAppError {
                    return Result.Error(it) //TODO better error handling
                }
        loadingMessages.forEach { message ->
            messageAiRepository.updateMessageAi(
                message.copy(
                    status = MessageStatus.ERROR,
                    message = "Error during last requesting"
                )
            )
        }
        return Result.Success(Unit)
    }
}