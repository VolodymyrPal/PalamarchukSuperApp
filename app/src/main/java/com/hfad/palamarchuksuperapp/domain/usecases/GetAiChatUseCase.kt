package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import javax.inject.Inject

interface GetAiChatUseCase {
    suspend operator fun invoke(chatId: Int): AppResult<MessageChat?, AppError>
}

class GetAiChatUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : GetAiChatUseCase {

    override suspend operator fun invoke(chatId: Int): AppResult<MessageChat?, AppError> {
        return chatController.getChatWithMessagesById(chatId)
    }
}