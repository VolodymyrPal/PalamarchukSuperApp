package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Clock

typealias Base64 = String

data class MessageGroup(
    val id: Int = 0,
    val role: Role = Role.USER,
    val content: PersistentList<MessageAI> = persistentListOf(),
    val chatId: Int,
    val type: MessageType = MessageType.TEXT,
) {
    constructor() : this(
        id = 0,
        role = Role.USER,
        content = persistentListOf(),
        chatId = 0
    )

    constructor(
        id: Int,
        role: Role = Role.USER,
        content: String,
        chatId: Int,
        type: MessageType = MessageType.TEXT,
    ) : this(
        id = id,
        role = role,
        content = persistentListOf(
            MessageAI(
                timestamp = Clock.System.now().toString(),
                message = content,
                messageGroupId = id,
            )
        ),
        chatId = chatId,
        type = type
    )

    constructor(
        id: Int,
        role: Role = Role.USER,
        content: String,
        otherContent: Base64,
        type: MessageType = MessageType.IMAGE,
        chatGroupId: Int,
    ) : this(
        id = id,
        role = role,
        content = persistentListOf(
            MessageAI(
                timestamp = Clock.System.now().toString(),
                message = content,
                otherContent = MessageAiContent.Image(otherContent),
                messageGroupId = id
            ),
        ),
        chatId = chatGroupId,
        type = type
    )

    constructor(
        id: Int,
        role: Role = Role.USER,
        content: List<MessageAI>,
        type: MessageType = MessageType.TEXT,
        chatId: Int,
    ) : this(
        id = id,
        role = role,
        content = content.toPersistentList(),
        chatId = chatId,
        type = type
    )

    constructor(
        id: Int,
        role: Role = Role.USER,
        type: MessageType = MessageType.TEXT,
        content: List<MessageAI>,
    ) : this(
        id = id,
        role = role,
        content = content.toPersistentList(),
        chatId = 0
    )

    companion object {

        fun from(messageGroupWithMessagesEntity: MessageGroupWithMessagesEntity): MessageGroup {
            return MessageGroup(
                id = messageGroupWithMessagesEntity.group.id,
                role = messageGroupWithMessagesEntity.group.role,
                content = messageGroupWithMessagesEntity.messages.map { MessageAI.from(it) }
                    .toPersistentList(),
                chatId = messageGroupWithMessagesEntity.group.chatId,
                type = messageGroupWithMessagesEntity.group.type
            )
        }
    }
}

enum class MessageType {
    TEXT,
    IMAGE
}

enum class Role(val value: String) {
    USER("user"),
    MODEL("model"),
    ASSISTANT("assistant"),
    PROMPT("prompt");
}