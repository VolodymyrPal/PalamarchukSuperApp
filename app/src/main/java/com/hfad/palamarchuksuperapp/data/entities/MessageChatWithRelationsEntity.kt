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
    val messageGroups: List<MessageGroupWithMessagesEntity> // Группы сообщений, связанные с чатом
) {
    fun toDomainModel(): MessageChat {
        return MessageChat(
            id = chat.id,
            name = chat.name,
            messageGroups = messageGroups.map { messageGroup -> messageGroup.toDomainModel() },
            timestamp = chat.timestamp
        )
    }
}

data class MessageGroupWithMessagesEntity(
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
            role = group.role,
            content = messages.map{ message -> message.toDomainModel() },
            type = group.type,
            chatId = group.chatId
        )
    }
    companion object {
        fun from(messageGroup: MessageGroup): MessageGroupWithMessagesEntity {
            return MessageGroupWithMessagesEntity(
                group = MessageGroupEntity.from(messageGroup),
                messages = messageGroup.content.map { aI -> MessageAiEntity.from(aI) },
            )
        }
    }
}