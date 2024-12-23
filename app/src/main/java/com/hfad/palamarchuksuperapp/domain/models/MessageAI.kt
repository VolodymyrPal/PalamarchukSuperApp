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
                message = content
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
                otherContent = MessageAiContent.Image(otherContent)
            ),
        ),
        type = type
    )
}

data class SubMessageAI(
    val id: Int = 0,
    val timestamp: String = Clock.System.now().toString(),
    val message: String = "",
    val otherContent: MessageAiContent? = null,
    val model: AiModel? = null,
    val isChosen: Boolean = false,
    val loading: Boolean = false,
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