package com.hfad.palamarchuksuperapp.domain.models

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class MessageChat(
    val id: Int = 0,
    val name: String = "",
    val messages: PersistentList<MessageGroup> = persistentListOf(),
    val lastModified: Long = System.currentTimeMillis(),
)