package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface UpdateMessageStatusUseCase {
    suspend operator fun invoke (chatId: Int, messageStatus: MessageStatus) : AppResult<Unit, AppError>
}

class UpdateMessageStatusUseCaseImpl @Inject constructor (
    private val chatController: ChatController,
): UpdateMessageStatusUseCase {
    override suspend fun invoke(chatId: Int, messageStatus: MessageStatus) : AppResult<Unit, AppError> {
        val loadingMessages =
            chatController.getAllMessagesWithStatus(chatId, MessageStatus.LOADING)
                .getOrHandleAppError {
                    return AppResult.Error(it) //TODO better error handling
                }
        loadingMessages.forEach { message ->
            chatController.updateMessageAi(
                message.copy(
                    status = MessageStatus.ERROR,
                    message = "Error during last requesting"
                )
            )
        }
        return AppResult.Success(Unit)
    }
}