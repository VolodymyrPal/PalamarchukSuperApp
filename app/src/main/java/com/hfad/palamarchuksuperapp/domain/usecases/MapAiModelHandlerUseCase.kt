package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.data.entities.LLMName
import com.hfad.palamarchuksuperapp.data.services.GeminiAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.data.services.GroqAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandlerFactory
import com.hfad.palamarchuksuperapp.domain.models.AiHandler
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import javax.inject.Inject

interface MapAiModelHandlerUseCase {
    suspend operator fun invoke(aiHandler: AiHandler): AiModelHandler
    suspend operator fun invoke(listAiHandler: List<AiHandler>): List<AiModelHandler>
}

class MapAiModelHandlerUseCaseImpl @Inject constructor(
    private val groqAIApiHandlerFactory: GroqAIApiHandlerFactory,
    private val geminiAIApiHandlerFactory: GeminiAIApiHandlerFactory,
    private val openAIApiHandlerFactory: OpenAIApiHandlerFactory
) : MapAiModelHandlerUseCase {
    override suspend fun invoke(aiHandler: AiHandler): AiModelHandler {
        return when (aiHandler.currentModel.llmName) {
            LLMName.GROQ -> {
                groqAIApiHandlerFactory.create(aiHandler)
            }
            LLMName.OPENAI -> {
                openAIApiHandlerFactory.create(aiHandler)
            }
            LLMName.GEMINI -> {
                geminiAIApiHandlerFactory.create(aiHandler)
            }
        }
    }

    override suspend fun invoke(listAiHandler: List<AiHandler>): List<AiModelHandler> {
        return listAiHandler.map { invoke(it) }
    }

}