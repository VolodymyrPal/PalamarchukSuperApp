package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role

@Entity( //TODO
    foreignKeys = [
        ForeignKey(
            entity = MessageChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val role: Role = Role.USER,
    val chatId: Int,
    val type: MessageType = MessageType.TEXT,
)