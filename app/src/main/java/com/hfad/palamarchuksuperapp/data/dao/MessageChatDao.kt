package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object для работы с сообщениями чата.
 * Обеспечивает все операции с базой данных для чатов, групп сообщений и AI сообщений.
 */
@Dao
interface MessageChatDao {

    // region Операции со связанными сущностями
    /**
     * Вставка чата со всеми связанными сущностями.
     * Создает чат, группы сообщений и сами сообщения с правильными связями между ними.
     * @param chatWithRelations Сущность чата со всеми связанными данными
     */
    @Transaction
    suspend fun insertChatWithRelations(
        chatWithRelations: MessageChatWithRelationsEntity,
    ): Long {
        val chatId = insertChat(chatWithRelations.chat)
        val group = chatWithRelations.messageGroupsWithMessageEntity
        group.forEach { messageGroup ->
            val groupId = insertMessageGroup(messageGroup.group.copy(chatId = chatId.toInt()))
            for (messageAi: MessageAiEntity in messageGroup.messages) {
                val insertedMessage = messageAi.copy(messageGroupId = groupId.toInt())
                insertMessage(insertedMessage)
            }
        }
        return chatId
    }

    /**
     * Вставка группы сообщений со всеми её сообщениями.
     * @param groupWithMessages Группа сообщений со связанными сообщениями
     * @return ID вставленной группы
     */
    @Transaction
    suspend fun insertMessageGroupWithMessages(
        groupWithMessages: MessageGroupWithMessagesEntity,
    ): Long {
        val groupId = insertMessageGroup(groupWithMessages.group)
        groupWithMessages.messages.forEach { messageAi ->
            val insertedMessage = messageAi.copy(messageGroupId = groupId.toInt())
            insertMessage(insertedMessage)
        }
        return groupId
    }

    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    fun chatWithMessagesFlow(chatId: Int): Flow<MessageChatWithRelationsEntity>

    /**
     * Обновление группы сообщений и всех её сообщений.
     * @param messageGroupWithMessages Группа с обновленными сообщениями
     */
    @Transaction
    suspend fun updateMessageGroupWithContent(
        messageGroupWithMessages: MessageGroupWithMessagesEntity,
    ) {
        updateMessageGroup(messageGroupWithMessages.group)
        messageGroupWithMessages.messages.forEach { messageAi ->
            val insertedMessage = messageAi.copy(messageGroupId = messageGroupWithMessages.group.id)
            insertMessage(insertedMessage)
        }
    }
    // endregion

    // region Операции с чатами
    /**
     * Получение всех чатов с их сообщениями.
     * @return Список всех чатов с связанными сообщениями
     */
    @Transaction
    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsWithMessages(): List<MessageChatWithRelationsEntity>

    /**
     * Получение конкретного чата по ID со всеми сообщениями.
     * @param chatId ID чата
     * @return Чат с связанными сообщениями
     */
    @Transaction
    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): MessageChatWithRelationsEntity

    /**
     * Получение информации о всех чатах без сообщений.
     * @return Список базовой информации о чатах
     */
    @Query("SELECT * FROM MessageChat")
    fun getAllChatsInfo(): Flow<List<MessageChatEntity>>

    /**
     * Создание нового чата.
     * @param chat Сущность чата
     * @return ID созданного чата
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageChatEntity): Long

    /**
     * Удаление чата.
     * Каскадно удаляет все связанные сообщения.
     * @param chat Сущность чата для удаления
     */
    @Delete
    suspend fun deleteChat(chat: MessageChatEntity)
    // endregion

    // region Операции с группами сообщений
    /**
     * Создание новой группы сообщений.
     * @param messageGroup Сущность группы
     * @return ID созданной группы
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageGroup(messageGroup: MessageGroupEntity): Long

    /**
     * Обновление группы сообщений.
     * @param messageGroup Обновленная сущность группы
     */
    @Update
    suspend fun updateMessageGroup(messageGroup: MessageGroupEntity)

    /**
     * Удаление группы сообщений по ID.
     * @param groupId ID группы для удаления
     */
    @Query("DELETE FROM MessageGroup WHERE id = :groupId")
    suspend fun deleteMessageGroup(groupId: Int)

    /**
     * Получение группы сообщений по ID со всеми сообщениями.
     * @param groupId ID группы
     * @return Группа с связанными сообщениями
     */
    @Query("SELECT * FROM MessageGroup WHERE id = :groupId")
    suspend fun getMessageGroup(groupId: Int): MessageGroupWithMessagesEntity
    // endregion

    // region Операции с AI сообщениями
    /**
     * Создание нового AI сообщения.
     * @param message Сущность сообщения
     * @return ID созданного сообщения
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageAiEntity): Long

    @Query("SELECT * FROM messageaientities WHERE id = :messageId")
    suspend fun getMessageById(messageId: Int): MessageAiEntity

    @Transaction
    suspend fun insertAndReturnMessage(message: MessageAiEntity): MessageAiEntity {
        val id = insertMessage(message)
        return getMessageById(id.toInt())
    }

    /**
     * Обновление AI сообщения.
     * @param message Обновленная сущность сообщения
     */
    @Update
    suspend fun updateMessage(message: MessageAiEntity)

    /**
     * Удаление AI сообщения по ID.
     * @param messageId ID сообщения для удаления
     */
    @Query("DELETE FROM messageaientities WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    /**
     * Получение всех сообщений для конкретной группы.
     * @param groupId ID группы
     * @return Список сообщений группы
     */
    @Query("SELECT * FROM messageaientities WHERE messageGroupId = :groupId")
    suspend fun getMessagesForGroup(groupId: Int): List<MessageAiEntity>
    // endregion
}