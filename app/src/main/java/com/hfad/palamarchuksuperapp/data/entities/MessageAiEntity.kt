package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.AiModel

typealias MessageAiContentString = String
/**
 * Сущность, представляющая отдельное AI сообщение.
 * Связана с MessageGroupEntity через внешний ключ messageGroupId.
 */
@Entity(
    tableName = "MessageAI",
    foreignKeys = [
        ForeignKey(
            entity = MessageGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageGroupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["messageGroupId"])]
)
data class MessageAiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val messageGroupId: Int,
    val timestamp: String,
    val message: String = "",
    val otherContent: MessageAiContentString? = null,
    val model: AiModel? = null,
    val isChosen: Boolean = false,
    val loading: Boolean = false
) 