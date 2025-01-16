package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import kotlinx.serialization.json.Json

typealias MessageAiContentString = String

/**
 * Сущность, представляющая отдельное AI сообщение.
 * Связана с MessageGroupEntity через внешний ключ messageGroupId.
 */
@Entity(
    tableName = "MessageAiEntities",
    foreignKeys = [
        ForeignKey(
            entity = MessageGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageGroupId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
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
    val status: MessageStatus = MessageStatus.CREATED,
) {
    companion object {
        fun toDomainModel(messageAiEntity: MessageAiEntity): MessageAI {
            return MessageAI(
                id = messageAiEntity.id,
                messageGroupId = messageAiEntity.messageGroupId,
                timestamp = messageAiEntity.timestamp,
                message = messageAiEntity.message,
                otherContent = messageAiEntity.otherContent?.let {
                    Json.decodeFromString<MessageAiContent>(
                        it
                    )
                },
                model = messageAiEntity.model,
                isChosen = messageAiEntity.isChosen,
                status = messageAiEntity.status
            )
        }

        fun from(messageAI: MessageAI) = MessageAiEntity(
            id = messageAI.id,
            messageGroupId = messageAI.messageGroupId,
            timestamp = messageAI.timestamp,
            message = messageAI.message,
            otherContent = messageAI.otherContent?.toString(),
            model = messageAI.model,
            isChosen = messageAI.isChosen,
            status = messageAI.status
        )
    }
}

enum class MessageStatus {
    CREATED,
    LOADING,
    SUCCESS,
    ERROR
}