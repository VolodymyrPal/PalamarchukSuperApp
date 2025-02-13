package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveAllChatsInfoUseCase {
    suspend operator fun invoke(): Result<Flow<List<MessageChat>>, AppError>
}

class ObserveAllChatsInfoUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : ObserveAllChatsInfoUseCase {

    override suspend fun invoke(): Result<Flow<List<MessageChat>>, AppError> {
        val allChatFlow = chatController.getAllChatsInfo().getOrHandleAppError {
            return Result.Error(it)
        }
        return Result.Success(allChatFlow)
    }
}
