package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.MessageChatWithMessages
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MessageChat( //TODO
    val id: Int = 0,
    val name: String = "",
    val messages: PersistentList<MessageGroup> = persistentListOf(),
    val lastModified: Long = System.currentTimeMillis(),
) {
    fun toEntity(): MessageChatWithMessages = this.toEntity()
}