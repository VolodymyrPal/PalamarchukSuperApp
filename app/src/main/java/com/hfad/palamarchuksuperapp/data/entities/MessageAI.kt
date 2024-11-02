package com.hfad.palamarchuksuperapp.data.entities

data class MessageAI(
    val role: Role = Role.USER,
    val content: String = "",
    val otherContent: Any = "",
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE
}

enum class Role(val value: String) {
    USER("user"),
    MODEL("model"),
    ASSISTANT("assistant"),
    SYSTEM("system"),
    PROMPT("prompt");
}