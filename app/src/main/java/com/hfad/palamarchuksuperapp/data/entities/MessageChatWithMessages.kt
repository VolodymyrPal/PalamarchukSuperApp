package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import kotlinx.collections.immutable.toPersistentList

data class MessageChatWithMessages( //TODO
    @Embedded val chat: MessageChatEntity,
    @Relation(
        entity = MessageGroupEntity::class,
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val messages: List<MessageGroupWithMessages>
) {
    fun toDomainModel(): MessageChat {
        return MessageChat(
            id = chat.id,
            name = chat.name,
            messages = messages.map { it.toDomainModel() }.toPersistentList(),
            lastModified = chat.lastModified
        )
    }
} 