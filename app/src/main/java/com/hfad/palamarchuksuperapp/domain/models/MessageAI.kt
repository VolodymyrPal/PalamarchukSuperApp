package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.MessageAiEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

data class MessageAI(
    val id: Int = 0,
    val messageGroupId: Int,
    val timestamp: String = Clock.System.now().toString(),
    val message: String = "",
    val otherContent: MessageAiContent? = null,
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

        fun toEntity(messageAI: MessageAI) = MessageAiEntity(
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

sealed class MessageAiContent {
    data class Image(val image: Base64) : MessageAiContent()
}
