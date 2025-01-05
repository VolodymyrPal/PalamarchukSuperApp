package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelations

@Dao
interface MessageChatDao {

    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsInfo(): List<MessageChatEntity>

    /**
     * Получение всех чатов с сообщениями.
     *
     * Transaction позволяет сохранить целосность между родительским и дочерним элементом
     * Без аннотации возможен исход, когда чат получен, а сообщения еще нет.
     */
    @Transaction
    @Query("SELECT * FROM MessageChat")
    fun getAllChatsWithMessages(): List<MessageChatWithRelations>

    @Transaction
    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): MessageChatWithRelations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageChatEntity): Long

    @Delete
    suspend fun deleteChat(chat: MessageChatEntity)

} 