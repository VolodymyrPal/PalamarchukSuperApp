package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import kotlinx.serialization.Serializable

@Serializable
data class AiHandler(
    val chosen : Boolean,
    val enabled : Boolean,
    val currentModel: AiModel
)

@Serializable
data class ListAiModelHandler(
    val aiHandlerList: List<AiHandler>
)