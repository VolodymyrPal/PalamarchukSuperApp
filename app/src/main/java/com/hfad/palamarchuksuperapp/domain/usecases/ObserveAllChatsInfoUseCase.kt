package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.domain.repository.ChatController
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveAllChatsInfoUseCase {
    suspend operator fun invoke(): AppResult<Flow<List<MessageChat>>, AppError>
}

class ObserveAllChatsInfoUseCaseImpl @Inject constructor(
    private val chatController: ChatController,
) : ObserveAllChatsInfoUseCase {

    override suspend fun invoke(): AppResult<Flow<List<MessageChat>>, AppError> {
        val allChatFlow = chatController.getAllChatsInfo().getOrHandleAppError {
            return AppResult.Error(it)
        }
        return AppResult.Success(allChatFlow)
    }
}
