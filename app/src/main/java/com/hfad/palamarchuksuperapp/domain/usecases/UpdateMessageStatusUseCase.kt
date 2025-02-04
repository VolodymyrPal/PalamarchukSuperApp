package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import javax.inject.Inject

interface UpdateMessageStatusUseCase {
    suspend operator fun invoke (chatId: Int, messageStatus: MessageStatus) : Result<Unit, AppError>
}

class UpdateMessageStatusUseCaseImpl @Inject constructor (
    private val chatAiRepository: ChatAiRepository
): UpdateMessageStatusUseCase {
    override suspend fun invoke(chatId: Int, messageStatus: MessageStatus) : Result<Unit, AppError> {
        val loadingMessages =
            chatAiRepository.getAllMessagesWithStatus(chatId, MessageStatus.LOADING)
                .getOrHandleAppError {
                    return Result.Error(it) //TODO better error handling
                }
        loadingMessages.forEach { message ->
            chatAiRepository.updateMessageAi(
                message.copy(
                    status = MessageStatus.ERROR,
                    message = "Error during last requesting"
                )
            )
        }
        return Result.Success(Unit)
    }
}