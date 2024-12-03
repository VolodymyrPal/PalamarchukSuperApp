package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.LLMName
import com.hfad.palamarchuksuperapp.data.services.GeminiAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.data.services.GroqAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface MapAiModelHandlerUseCase {
    suspend operator fun invoke(aiHandlerInfo: AiHandlerInfo): AiModelHandler
    suspend operator fun invoke(listAiHandlerInfo: List<AiHandlerInfo>): List<AiModelHandler>
}

class MapAiModelHandlerUseCaseImpl @Inject constructor(
    private val groqAIApiHandlerFactory: GroqAIApiHandlerFactory,
    private val geminiAIApiHandlerFactory: GeminiAIApiHandlerFactory,
    private val openAIApiHandlerFactory: OpenAIApiHandlerFactory
) : MapAiModelHandlerUseCase {
    override suspend fun invoke(aiHandlerInfo: AiHandlerInfo): AiModelHandler {
        return when (aiHandlerInfo.model.llmName) {
            LLMName.GROQ -> {
                groqAIApiHandlerFactory.create(aiHandlerInfo)
            }
            LLMName.OPENAI -> {
                openAIApiHandlerFactory.create(aiHandlerInfo)
            }
            LLMName.GEMINI -> {
                geminiAIApiHandlerFactory.create(aiHandlerInfo)
            }
        }
    }

    override suspend fun invoke(listAiHandlerInfo: List<AiHandlerInfo>): List<AiModelHandler> {
        return listAiHandlerInfo.map { invoke(it) }
    }

}