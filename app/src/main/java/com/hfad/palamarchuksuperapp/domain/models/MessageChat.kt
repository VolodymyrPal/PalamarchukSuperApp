package com.hfad.palamarchuksuperapp.domain.models

data class MessageChat(
    val id: Int = 0,
    val name: String = "",
    val messageGroups: List<MessageGroup> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
)