package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import kotlinx.collections.immutable.toPersistentList

data class MessageGroupWithMessages( //TODO
    @Embedded val group: MessageGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageGroupId"
    )
    val messages: List<MessageAiEntity>
) {
    fun toDomainModel(): MessageGroup {
        return MessageGroup(
            id = group.id,
            content = messages.map { it.toDomainModel() }.toPersistentList(),
            role = group.role,
            type = group.type,
            chatId = group.chatId
        )
    }
} 