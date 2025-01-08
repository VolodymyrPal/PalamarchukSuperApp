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
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessages
import com.hfad.palamarchuksuperapp.domain.models.MessageChat

@Dao
interface MessageChatDao {

    // Операции с чатами
    /**Получение всех чатов с сообщениями.
     *
     * Transaction позволяет сохранить целосность между родительским и дочерним элементом
     * Без аннотации возможен исход, когда чат получен, а сообщения еще нет. */

    // Операции со связанными сущностями
    @Transaction
    suspend fun insertChatWithRelations(
        chat: MessageChat,
    ) {
        insertChat(MessageChatEntity.from(chat))
        val group = chat.messages //Need better inserting logic
        for (messageGroup in group) {
            insertMessageGroup(MessageGroupEntity.from(messageGroup))
            for (messageAi in messageGroup.content) {
                insertMessage(MessageAiEntity.from(messageAi))
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsWithMessages(): List<MessageChatWithRelationsEntity>

    @Transaction
    @Query("SELECT * FROM MessageChat WHERE id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): MessageChatWithRelationsEntity

    @Query("SELECT * FROM MessageChat")
    suspend fun getAllChatsInfo(): List<MessageChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageChatEntity): Long

    @Delete
    suspend fun deleteChat(chat: MessageChatEntity)

    // Операции с группами сообщений
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageGroup(messageGroup: MessageGroupEntity): Long

    @Update
    suspend fun updateMessageGroup(messageGroup: MessageGroupEntity)

    @Query("DELETE FROM MessageGroup WHERE id = :groupId")
    suspend fun deleteMessageGroup(groupId: Int)

    @Query("SELECT * FROM MessageGroup WHERE id = :groupId")
    suspend fun getMessageGroup(groupId: Int): MessageGroupWithMessages

    // Операции с AI сообщениями
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageAiEntity): Long

    @Update
    suspend fun updateMessage(message: MessageAiEntity)

    @Query("DELETE FROM MessageAI WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("SELECT * FROM MessageAI WHERE messageGroupId = :groupId")
    suspend fun getMessagesForGroup(groupId: Int): List<MessageAiEntity>
}