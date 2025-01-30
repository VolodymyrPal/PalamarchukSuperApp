package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ObserveChatAiUseCase {
    suspend operator fun invoke(chatId: Int): Result<Flow<MessageChat>, AppError>
}

class ObserveChatAiUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
    private val updateMessageStatusUseCase: UpdateMessageStatusUseCase,
) : ObserveChatAiUseCase {

    override suspend operator fun invoke(chatId: Int): Result<Flow<MessageChat>, AppError> {
        val existingChats = chatAiRepository.getAllChatsInfo()
            .getOrHandleAppError { return Result.Error(it) }

        val chatFlow = when {

            chatExists(chatId) -> chatAiRepository.getChatFlowById(chatId)
                .getOrHandleAppError { return Result.Error(it) }

            chatId == 0 && existingChats.isNotEmpty() -> {
                val lastChatId = existingChats.lastOrNull()?.id
                    ?: return Result.Error(AppError.CustomError("No chats"))
                chatAiRepository.getChatFlowById(lastChatId)
                    .getOrHandleAppError { return Result.Error(it) }
            }

            else -> {
                val newChatId = createNewChat()
                    .getOrHandleAppError { return Result.Error(it) }.id
                chatAiRepository.getChatFlowById(newChatId)
                    .getOrHandleAppError { return Result.Error(it) }
            }
        }

        coroutineScope {
            launch(Dispatchers.Default) {
                updateMessageStatusUseCase.invoke(chatId, MessageStatus.LOADING)
                    .getOrHandleAppError { return@launch }
            }
        }
        return Result.Success(chatFlow)

    }

    private suspend fun chatExists(chatId: Int): Boolean {
        val isExist = chatAiRepository.isChatExist(chatId)
            .getOrHandleAppError { return false }

        return isExist
    }

    /** Создает новый чат с тестовыми данными и возвращает его ID */
    private suspend fun createNewChat(): Result<MessageChat, AppError> {
//        val id = chatAiRepository.createChat(MessageChat(name = "Base chat")) TODO for product
        val newChat = chatAiRepository.addAndGetChat(     //TODO for testing chat
            MessageChat(
                name = "Base chat",
                messageGroups = MockChat()
            )
        ).getOrHandleAppError { return Result.Error(it) }
        return Result.Success(newChat)
    }

}