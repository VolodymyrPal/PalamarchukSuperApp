package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role

/**
 * Сущность, представляющая группу сообщений в чате.
 * Связана с MessageChatEntity через внешний ключ chatId.
 */
@Entity(
    tableName = "MessageGroup",
    foreignKeys = [
        ForeignKey(
            entity = MessageChatEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chatId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatId"])]
)
data class MessageGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val role: Role = Role.USER,
    val chatId: Int,
    val type: MessageType = MessageType.TEXT,
) {
    companion object {
        fun from(chat: MessageGroup): MessageGroupEntity {
            return MessageGroupEntity(
                id = chat.id,
                role = chat.role,
                chatId = chat.chatId,
                type = chat.type
            )
        }
    }
}