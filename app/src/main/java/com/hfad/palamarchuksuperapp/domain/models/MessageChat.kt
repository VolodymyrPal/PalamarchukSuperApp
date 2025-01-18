package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.MessageChatEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithRelationsEntity

data class MessageChat(
    val id: Int = 0,
    val name: String = "New chat",
    val messageGroups: List<MessageGroup> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        fun from(messageChatEntity: MessageChatEntity): MessageChat {
            return MessageChat(
                name = messageChatEntity.name,
                timestamp = messageChatEntity.timestamp,
                id = messageChatEntity.id
            )
        }

        fun from(messageChatWithRelationsEntity: MessageChatWithRelationsEntity): MessageChat {
            return MessageChat(
                name = messageChatWithRelationsEntity.chat.name,
                timestamp = messageChatWithRelationsEntity.chat.timestamp,
                id = messageChatWithRelationsEntity.chat.id,
                messageGroups = messageChatWithRelationsEntity.messageGroupsWithMessageEntity.map {
                    MessageGroup.from(
                        it
                    )
                }
            )
        }
    }
}