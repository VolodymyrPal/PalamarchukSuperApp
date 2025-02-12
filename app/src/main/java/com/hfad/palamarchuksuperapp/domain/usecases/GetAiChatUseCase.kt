package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import javax.inject.Inject

interface GetAiChatUseCase {
    suspend operator fun invoke(chatId: Int): Result<MessageChat?, AppError>
}

class GetAiChatUseCaseImpl @Inject constructor(
    private val messageChatRepository: MessageChatRepository,
) : GetAiChatUseCase {

    override suspend operator fun invoke(chatId: Int): Result<MessageChat?, AppError> {
        return messageChatRepository.getChatWithMessagesById(chatId)
    }
}