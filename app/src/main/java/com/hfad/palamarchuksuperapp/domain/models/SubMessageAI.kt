package com.hfad.palamarchuksuperapp.domain.models

import kotlinx.datetime.Clock

data class SubMessageAI(
    val id: Int = 0,
    val messageAiID: Int,
    val timestamp: String = Clock.System.now().toString(),
    val message: String = "",
    val otherContent: MessageAiContent? = null,
    val model: AiModel? = null,
    val isChosen: Boolean = false,
    val loading: Boolean = false,
)

sealed class MessageAiContent {
    data class Image(val image: Base64) : MessageAiContent()
}
