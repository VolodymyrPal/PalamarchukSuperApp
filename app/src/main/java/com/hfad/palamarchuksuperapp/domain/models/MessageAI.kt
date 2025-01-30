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
        fun from(messageAI: MessageAiEntity): MessageAI {
            return MessageAI(
                id = messageAI.id,
                messageGroupId = messageAI.messageGroupId,
                timestamp = messageAI.timestamp,
                message = messageAI.message,
                otherContent = messageAI.otherContent?.let { //TODO other content not supported
                    Json.decodeFromString<MessageAiContent>(
                        it
                    )
                },
                model = messageAI.model,
                isChosen = messageAI.isChosen,
                status = messageAI.status
            )
        }
    }
}

sealed class MessageAiContent {
    data class Image(val image: Base64) : MessageAiContent()
}
