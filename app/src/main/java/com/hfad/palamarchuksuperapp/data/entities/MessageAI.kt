package com.hfad.palamarchuksuperapp.data.entities

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock

typealias Base64 = String

data class MessageAI(
    val role: Role = Role.USER,
    val content: PersistentList<SubMessageAI> = persistentListOf(),
    val type: MessageType = MessageType.TEXT,
) {
    constructor(
        role: Role = Role.USER, content: String, type: MessageType = MessageType.TEXT,
    ) : this(
        role = role,
        content = persistentListOf(
            SubMessageAI(
                timestamp = Clock.System.now().toString(),
                message = content
            )
        ),
        type = type
    )

    constructor(
        role: Role = Role.USER,
        content: String,
        otherContent: Base64,
        type: MessageType = MessageType.IMAGE,
    ) : this(
        role = role,
        content = persistentListOf(
            SubMessageAI(
                timestamp = Clock.System.now().toString(),
                message = content,
                otherContent = MessageAiContent.Image(otherContent)
            ),
        ),
        type = type
    )
}

data class SubMessageAI(
    val id: String = "",
    val timestamp: String = Clock.System.now().toString(),
    val message: String = "",
    val otherContent: MessageAiContent? = null,
    val model: AiModel? = null,
    val isChosen: Boolean = false,
)

sealed class MessageAiContent {
    data class Image(val image: Base64) : MessageAiContent()
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