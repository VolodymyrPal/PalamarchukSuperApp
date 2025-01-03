package com.hfad.palamarchuksuperapp.domain.models

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
    constructor(
        id: Int,
        role: Role = Role.USER,
        content: String,
        type: MessageType = MessageType.TEXT,
        chatGroupId: Int,
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
        chatId = chatGroupId,
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