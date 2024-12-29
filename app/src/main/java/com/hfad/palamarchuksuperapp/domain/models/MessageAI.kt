package com.hfad.palamarchuksuperapp.domain.models

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock

typealias Base64 = String

data class MessageAI(
    val id: Int = 0,
    val role: Role = Role.USER,
    val content: PersistentList<SubMessageAI> = persistentListOf(),
    val type: MessageType = MessageType.TEXT,
) {
    constructor(
        id: Int, role: Role = Role.USER, content: String, type: MessageType = MessageType.TEXT,
    ) : this(
        id = id,
        role = role,
        content = persistentListOf(
            SubMessageAI(
                timestamp = Clock.System.now().toString(),
                message = content,
                messageAiID = id
            )
        ),
        type = type
    )

    constructor(
        id: Int,
        role: Role = Role.USER,
        content: String,
        otherContent: Base64,
        type: MessageType = MessageType.IMAGE,
    ) : this(
        id = id,
        role = role,
        content = persistentListOf(
            SubMessageAI(
                timestamp = Clock.System.now().toString(),
                message = content,
                otherContent = MessageAiContent.Image(otherContent),
                messageAiID = id
            ),
        ),
        type = type
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