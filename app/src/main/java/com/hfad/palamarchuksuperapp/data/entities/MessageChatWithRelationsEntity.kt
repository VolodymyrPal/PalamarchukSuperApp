package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.hfad.palamarchuksuperapp.domain.models.MessageChat
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup

data class MessageChatWithRelationsEntity(
    @Embedded
    val chat: MessageChatEntity, // Данные чата

    @Relation(
        parentColumn = "id",
        entityColumn = "chatId",
        entity = MessageGroupEntity::class
    )
    val messages: List<MessageGroupWithMessages> // Группы сообщений, связанные с чатом
) {
    fun toDomainModel(): MessageChat {
        return MessageChat(
            id = chat.id,
            name = chat.name,
            messages = messages.map { messages -> messages.toDomainModel() }
        )
    }
}

data class MessageGroupWithMessages(
    @Embedded
    val group: MessageGroupEntity, // Данные группы сообщений

    @Relation(
        parentColumn = "id",
        entityColumn = "messageGroupId",
        entity = MessageAiEntity::class
    )
    val messages: List<MessageAiEntity> // Сообщения, связанные с группой
) {
    fun toDomainModel(): MessageGroup {
        return MessageGroup(
            id = group.id,
            content = messages.map{ it.toDomainModel() },
            role = group.role,
            type = group.type,
            chatId = group.chatId
        )
    }
}