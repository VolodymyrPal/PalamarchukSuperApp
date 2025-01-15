package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
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
    val messageGroupsWithMessageEntity: List<MessageGroupWithMessagesEntity>, // Группы сообщений, связанные с чатом
) {
    fun toDomainModel(): MessageChat {
        return MessageChat(
            id = chat.id,
            name = chat.name,
            messageGroups = messageGroupsWithMessageEntity.map { messageGroup ->
                MessageGroupWithMessagesEntity.toDomainModel(
                    messageGroup
                )
            },
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
    val messages: List<MessageAiEntity>, // Сообщения, связанные с группой
) {
    companion object {
        fun toDomainModel(messageGroupEntity: MessageGroupWithMessagesEntity): MessageGroup {
            return MessageGroup(
                id = messageGroupEntity.group.id,
                role = messageGroupEntity.group.role,
                content = messageGroupEntity.messages.map { message ->
                    MessageAI.toDomainModel(
                        message
                    )
                },
                type = messageGroupEntity.group.type,
                chatId = messageGroupEntity.group.chatId
            )
        }

        fun from(messageGroup: MessageGroup): MessageGroupWithMessagesEntity {
            return MessageGroupWithMessagesEntity(
                group = MessageGroupEntity.from(messageGroup),
                messages = messageGroup.content.map { aI -> MessageAiEntity.from(aI) },
            )
        }
    }
}