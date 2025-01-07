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
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelations
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessages

@Dao
interface MessageChatDao {

    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsInfo(): List<MessageChatEntity>

    /**Получение всех чатов с сообщениями.
     *
     * Transaction позволяет сохранить целосность между родительским и дочерним элементом
     * Без аннотации возможен исход, когда чат получен, а сообщения еще нет. */
    @Transaction
    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsWithMessages(): List<MessageChatWithRelations>

    @Transaction
    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): MessageChatWithRelations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageChatEntity): Long

    @Delete
    suspend fun deleteChat(chat: MessageChatEntity)

    // Методы для работы с группами сообщений
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageGroup(messageGroup: MessageGroupEntity): Long

    @Update
    suspend fun updateMessageGroup(messageGroup: MessageGroupEntity)

    @Query("DELETE FROM MessageGroup WHERE id = :groupId")
    suspend fun deleteMessageGroup(groupId: Int)

    // Методы для работы с AI сообщениями
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageAiEntity): Long

    @Update
    suspend fun updateMessage(message: MessageAiEntity)

    @Query("DELETE FROM MessageAI WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("SELECT * FROM MessageAI WHERE messageGroupId = :groupId")
    suspend fun getMessagesForGroup(groupId: Int): List<MessageAiEntity>

    @Query("SELECT * FROM MessageGroup WHERE id = :groupId")
    suspend fun getMessageGroup(groupId: Int): MessageGroupWithMessages
}