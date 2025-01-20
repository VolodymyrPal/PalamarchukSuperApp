package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ObserveChatAiUseCase {
    suspend operator fun invoke(chatId: Int): Flow<MessageChat>
}

class ObserveChatAiUseCaseImpl @Inject constructor(
    private val chatAiRepository: ChatAiRepository,
) : ObserveChatAiUseCase {

    override suspend operator fun invoke(chatId: Int): Flow<MessageChat> {
        val chatFlow = if (isChatMissing(chatId)) {
            val newChatId = createNewChat()
            chatAiRepository.getChatFlowById(newChatId)
        } else {
            chatAiRepository.getChatFlowById(chatId)
        }
        coroutineScope {
            launch(Dispatchers.Default) {
                handleLoadingMessages(chatId)
            }
        }
        return chatFlow
    }


    /** Проверяет, существует ли чат с данным chatId */
    private suspend fun isChatMissing(chatId: Int): Boolean {
        return chatAiRepository.getAllChats().find { it.id == chatId } == null
    }

    /** Создает новый чат с тестовыми данными и возвращает его ID */
    private suspend fun createNewChat(): Int {
        // val id = chatAiRepository.createChat(MessageChat(name = "Base chat")) TODO for product
        val id = chatAiRepository.addChatWithMessages(     //TODO for testing chat
            MessageChat(
                name = "Base chat",
                messageGroups = MockChat()
            )
        )
        return id.toInt()
    }

    /** Обрабатывает сообщения со статусом LOADING и обновляет их статус на ERROR */
    private suspend fun handleLoadingMessages(chatId: Int) {
        val chat = chatAiRepository.getChatWithMessagesById(chatId)
        chat.messageGroups.forEach { group ->
            group.content.forEach { message ->
                if (message.status == MessageStatus.LOADING) {
                    chatAiRepository.updateMessageAi(
                        message.copy(
                            status = MessageStatus.ERROR,
                            message = "Error during last requesting"
                        )
                    )
                }
            }
        }
    }
}