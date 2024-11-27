package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.LLMName
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class AiHandler(
    val llmName : LLMName,
    val chosen : Boolean,
    val enabled : Boolean,
    @Polymorphic val model: AiModel
)