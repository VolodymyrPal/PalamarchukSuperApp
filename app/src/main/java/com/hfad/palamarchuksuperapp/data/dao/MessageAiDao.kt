package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity

@Dao
interface MessageAiDao {

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

    @Query(
        "SELECT ma.* " +
                "FROM MessageAiEntities AS ma " +
                "INNER JOIN MessageGroup AS mg " +
                "WHERE mg.chatId = :chatId AND ma.status = :status "
    )
    suspend fun getMessagesWithStatus(chatId: Int, status: String): List<MessageAiEntity>
}