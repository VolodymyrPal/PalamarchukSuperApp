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
        val chatFlow = if (isChatMissing(chatId)) {
            val newChatId = createNewChat().onSuccessOrReturnAppError {
                return Result.Error(it)
            }
            val chat = chatAiRepository.getChatFlowById(newChatId).onSuccessOrReturnAppError {
                return Result.Error(it)
            }
            chat
        } else {
            chatAiRepository.getChatFlowById(chatId).onSuccessOrReturnAppError {
                return Result.Error(it)
            }
        }
        coroutineScope {
            launch(Dispatchers.Default) {
                updateMessageStatusUseCase(chatId, MessageStatus.LOADING)
            }
        }
        return Result.Success(chatFlow)
    }


    /** Проверяет, существует ли чат с данным chatId */
    private suspend fun isChatMissing(chatId: Int): Boolean {
        val bool = chatAiRepository.getAllChats().onSuccessOrReturnAppError { return false }
            .find { it.id == chatId } == null

        return bool
    }

    /** Создает новый чат с тестовыми данными и возвращает его ID */
    private suspend fun createNewChat(): Result<Int, AppError> {
        // val id = chatAiRepository.createChat(MessageChat(name = "Base chat")) TODO for product
        val id = chatAiRepository.addChatWithMessages(     //TODO for testing chat
            MessageChat(
                name = "Base chat",
                messageGroups = MockChat()
            )
        ).onSuccessOrReturnAppError { return Result.Error(it) }
        return Result.Success(id.toInt())
    }

}