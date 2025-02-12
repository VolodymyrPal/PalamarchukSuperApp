package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
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

    @Query("SELECT EXISTS(SELECT 1 FROM MessageChat WHERE id = :chatId)")
    suspend fun isExist(chatId: Int): Boolean

    /**
     * Удаление чата.
     * Каскадно удаляет все связанные сообщения.
     * @param chatId Сущность ID чата для удаления
     */
    @Query("DELETE FROM MessageChat WHERE id = :chatId")
    suspend fun deleteChat(chatId: Int)

    @Query("DELETE FROM MessageChat")
    suspend fun deleteAllChats()

    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    fun observeChatWithMessagesFlow(chatId: Int): Flow<MessageChatWithRelationsEntity>

}