package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.MessageChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ObserveChatAiUseCase {
    suspend operator fun invoke(chatId: Int): Result<Flow<MessageChat>, AppError>
}

class ObserveChatAiUseCaseImpl @Inject constructor(
    private val messageChatRepository: MessageChatRepository,
    private val updateMessageStatusUseCase: UpdateMessageStatusUseCase,
) : ObserveChatAiUseCase {

    override suspend operator fun invoke(chatId: Int): Result<Flow<MessageChat>, AppError> {
        val existingChats = messageChatRepository.getAllChatsInfo()
            .getOrHandleAppError { return Result.Error(it) }.firstOrNull()

        val chatFlow = when {

            chatExists(chatId) -> messageChatRepository.getChatFlowById(chatId)
                .getOrHandleAppError { return Result.Error(it) }

            chatId == 0 && existingChats != null && existingChats.isNotEmpty() -> {
                val lastChatId = existingChats.lastOrNull()?.id
                    ?: return Result.Error(AppError.CustomError("No chats"))
                messageChatRepository.getChatFlowById(lastChatId)
                    .getOrHandleAppError { return Result.Error(it) }
            }

            else -> {
                val newChatId = createNewChat()
                    .getOrHandleAppError { return Result.Error(it) }.id
                messageChatRepository.getChatFlowById(newChatId)
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
        val isExist = messageChatRepository.isChatExist(chatId)
            .getOrHandleAppError { return false }

        return isExist
    }

    /** Создает новый чат с тестовыми данными и возвращает его ID */
    private suspend fun createNewChat(): Result<MessageChat, AppError> {
//        val messageChat: MessageChat =
//            chatAiRepository.addAndGetChat(MessageChat(name = "Base chat")).getOrHandleAppError {
//                return Result.Error(it)
//            } //TODO for product
        val messageChat = messageChatRepository.addAndGetChat(     //TODO for testing chat
            MessageChat(
                name = "Base chat",
                messageGroups = MockChat()
            )
        ).getOrHandleAppError { return Result.Error(it) }
        return Result.Success(messageChat)
    }

}