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
    companion object {
        fun from(messageChat: MessageChat): MessageChatWithRelationsEntity {
            return MessageChatWithRelationsEntity(
                chat = MessageChatEntity.from(messageChat),
                messageGroupsWithMessageEntity = messageChat.messageGroups.map {
                    MessageGroupWithMessagesEntity.from(it)
                }
            )
        }
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
        fun from(messageGroup: MessageGroup): MessageGroupWithMessagesEntity {
            return MessageGroupWithMessagesEntity(
                group = MessageGroupEntity.from(messageGroup),
                messages = messageGroup.content.map { aI -> MessageAiEntity.from(aI) },
            )
        }
    }
}