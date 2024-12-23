package com.hfad.palamarchuksuperapp.data.dtos

import com.hfad.palamarchuksuperapp.domain.models.LLMName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIModelDTO(
    val llmName: LLMName,
    val modelName: String,
    val isSupported: Boolean
)