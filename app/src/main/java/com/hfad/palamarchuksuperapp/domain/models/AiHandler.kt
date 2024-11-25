package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.AiProviderName

data class AiHandler(
    val modelName : AiProviderName,
    val chosen : Boolean,
    val enabled : Boolean,
    val model: AiModel
)