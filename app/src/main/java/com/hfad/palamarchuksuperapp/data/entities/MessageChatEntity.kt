package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.data.database.DATABASE_MESSAGE_CHAT

@Entity(tableName = DATABASE_MESSAGE_CHAT)
data class MessageChatEntity( //TODO
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val lastModified: Long = System.currentTimeMillis(),
)
