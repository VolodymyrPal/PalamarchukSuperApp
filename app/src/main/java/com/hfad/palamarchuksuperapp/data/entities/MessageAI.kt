package com.hfad.palamarchuksuperapp.data.entities

data class MessageAI(
    val role: String = "user",
    val content: String = "",
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE
}
