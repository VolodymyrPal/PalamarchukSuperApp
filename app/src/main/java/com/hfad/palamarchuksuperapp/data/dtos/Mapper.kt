package com.hfad.palamarchuksuperapp.data.dtos

import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.Product
import com.hfad.palamarchuksuperapp.domain.models.Skill

fun GeminiModelDTO.toGeminiModel(): AiModel.GeminiModel {
    return AiModel.GeminiModel(
        llmName = LLMName.GEMINI,
        modelName = modelName.replace(
            "models/",
            ""
        ),
        isSupported = supportedGenerationMethods.contains("generateContent")
    )
}

fun GroqModelDTO.toGroqModel(): AiModel.GroqModel {
    return AiModel.GroqModel(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}

fun OpenAIModelDTO.toOpenAIModel(): AiModel.OpenAIModel {
    return AiModel.OpenAIModel(
        llmName = llmName,
        modelName = modelName,
        isSupported = isSupported
    )
}

fun SkillEntity.toSkill() = Skill(
    id = id,
    uuid =uuid,
    name = name,
    description = description,
    date = date,
    position = position,
    chosen = false,
    isExpanded = false,
    isVisible = true
)

fun Skill.toSkillEntity() = SkillEntity(
    id = id,
    uuid = uuid,
    name = name,
    description = description,
    date = date,
    position = position
)

fun ProductDTO.toProduct() = Product(
    productDTO = this,
    id = this.id
)