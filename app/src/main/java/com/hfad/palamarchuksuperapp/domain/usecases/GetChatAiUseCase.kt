package com.hfad.palamarchuksuperapp.domain.usecases

import android.util.Log
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.data.repository.MockChat
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

interface GetChatAiUseCase {
    suspend operator fun invoke(chatId: Int): Flow<MessageChat>
}

class GetChatAiUseCaseImpl @Inject constructor(
    private val messageChatDao: MessageChatDao,
) : GetChatAiUseCase {

    override suspend operator fun invoke(chatId: Int): Flow<MessageChat> {
        if (isChatMissing(chatId)) {
            val newChatId = createNewChatWithRelations()
            return messageChatDao.getChatWithMessagesFlow(newChatId)
                .map { MessageChatWithRelationsEntity.toDomain(it) }
                .onStart { handleLoadingMessages(chatId) }
        } else {
            return messageChatDao.getChatWithMessagesFlow(chatId)
                .map { MessageChatWithRelationsEntity.toDomain(it) }
                .onStart { handleLoadingMessages(chatId) }
        }
    }

    /** Проверяет, существует ли чат с данным chatId */
    private suspend fun isChatMissing(chatId: Int): Boolean {
        return messageChatDao.getAllChatsInfo().find { it.id == chatId } == null
    }

    /** Создает новый чат с тестовыми данными и возвращает его ID */
    private suspend fun createNewChatWithRelations(): Int {
        val id = messageChatDao.insertChatWithRelations(
            MessageChatWithRelationsEntity(
                chat = MessageChatEntity(name = "Base chat"),
                messageGroupsWithMessageEntity = MockChat().map {
                    MessageGroupWithMessagesEntity.from(it)
                }
            )
        )
        Log.d("ChatAiRepositoryImpl", "getChatById: $id")
        return id.toInt()
    }

    /** Обрабатывает сообщения со статусом LOADING и обновляет их статус на ERROR */
    private suspend fun handleLoadingMessages(chatId: Int) {
        val chat = MessageChatWithRelationsEntity.toDomain(
            messageChatDao.getChatWithMessages(chatId)
        )
        chat.messageGroups.forEach { group ->
            group.content.forEach { message ->
                if (message.status == MessageStatus.LOADING) {
                    messageChatDao.updateMessage(
                        MessageAiEntity.from(message).copy(
                            status = MessageStatus.ERROR,
                            message = "Error during last requesting"
                        )
                    )
                }
            }
        }
    }

}