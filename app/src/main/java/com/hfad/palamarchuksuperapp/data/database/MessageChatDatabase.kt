package com.hfad.palamarchuksuperapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.dao.MessageAiDao
import com.hfad.palamarchuksuperapp.data.dao.MessageChatDao
import com.hfad.palamarchuksuperapp.data.dao.MessageGroupDao
import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity

@Database(
    entities = [MessageChatEntity::class, MessageGroupEntity::class, MessageAiEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(MessageChatConverters::class)
abstract class MessageChatDatabase : RoomDatabase() {
    abstract fun messageChatDao(): MessageChatDao
    abstract fun messageGroupDao(): MessageGroupDao
    abstract fun messageAiDao(): MessageAiDao
}

const val DATABASE_MESSAGE_CHAT = "message_chat_database" 