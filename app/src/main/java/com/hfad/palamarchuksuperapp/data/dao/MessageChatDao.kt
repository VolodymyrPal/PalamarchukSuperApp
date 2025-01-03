package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MESSAGE_CHAT
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithMessages

@Dao
interface MessageChatDao { //TODO
    @Query("SELECT * FROM $DATABASE_MESSAGE_CHAT ORDER BY lastModified DESC")
    fun getAllChats(): List<MessageChatEntity>

    @Query("SELECT * FROM $DATABASE_MESSAGE_CHAT WHERE id = :chatId")
    suspend fun getChatById(chatId: Int): MessageChatEntity?

    @Transaction
    @Query("SELECT * FROM $DATABASE_MESSAGE_CHAT WHERE id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): MessageChatWithMessages?

    @Transaction
    @Query("SELECT * FROM $DATABASE_MESSAGE_CHAT")
    fun getAllChatsWithMessages(): List<MessageChatWithMessages>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageChatEntity)

    @Update
    suspend fun updateChat(chat: MessageChatEntity)

    @Query("DELETE FROM $DATABASE_MESSAGE_CHAT WHERE id = :chatId")
    suspend fun deleteChat(chatId: Int)

    @Query("DELETE FROM $DATABASE_MESSAGE_CHAT")
    suspend fun clearAllChats()
} 