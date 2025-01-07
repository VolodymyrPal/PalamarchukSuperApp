package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.MessageChat

/**
 * Сущность, представляющая чат в базе данных.
 * Является корневой таблицей для хранения информации о чатах.
 */
@Entity(tableName = "MessageChat")
data class MessageChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        fun from(chat: MessageChat): MessageChatEntity {
            return MessageChatEntity(
                name = chat.name,
                timestamp = chat.timestamp
            )
        }

        fun toDomain(messageChatEntity: MessageChatEntity): MessageChat {
            return MessageChat(
                name = messageChatEntity.name,
                timestamp = messageChatEntity.timestamp,
                id = messageChatEntity.id
            )
        }
    }
}