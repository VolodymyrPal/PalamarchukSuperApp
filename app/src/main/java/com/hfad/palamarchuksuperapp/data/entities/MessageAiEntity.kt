package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import kotlinx.serialization.json.Json

typealias MessageAiContentString = String

@Entity( //TODO
    foreignKeys = [
        ForeignKey(
            entity = MessageGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageGroupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
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
    val loading: Boolean = false,
) {
    fun toDomainModel(): MessageAI {
        return MessageAI(
            id = id,
            messageGroupId = messageGroupId,
            timestamp = timestamp,
            message = message,
            otherContent = otherContent?.let { string ->
                Json.decodeFromString<MessageAiContent?>(
                    string
                )
            },
            model = model,
            isChosen = isChosen,
            loading = loading
        )
    }
}