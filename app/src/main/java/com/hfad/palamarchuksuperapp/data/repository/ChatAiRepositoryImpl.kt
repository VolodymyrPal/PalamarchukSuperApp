package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.services.GeminiApiHandler
import com.hfad.palamarchuksuperapp.data.services.GroqApiHandler
import com.hfad.palamarchuksuperapp.data.services.OpenAIApiHandler
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.repository.ChatAiRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class ChatAiRepositoryImpl @Inject constructor(
    private val groqApiHandler: GroqApiHandler,
    private val geminiApiHandler: GeminiApiHandler,
    private val openAIApiHandler: OpenAIApiHandler,
) : ChatAiRepository {

    override val chatAiChatFlow: MutableStateFlow<PersistentList<MessageAI>> =
        MutableStateFlow(persistentListOf())

    override val errorFlow: MutableStateFlow<DataError?> = MutableStateFlow(null)

    override suspend fun getRespondChatOrImage(message: String) {
        val userMessage = MessageAI(role = "user", content = message)
        try {


            chatAiChatFlow.update { chatAiChatFlow.value.add(userMessage) }
        } catch (e: Exception) {
            errorFlow.emit(DataError.CustomError(e.message ?: "Error"))
        }

    }

    private suspend fun sendRequestToAI(message: String) {

//        when (currentModel) {
//            is AiModels.GroqModels -> groqApiHandler.getRespondChatOrImage(message)
//
//            is AiModels.GeminiModels,
//                -> geminiApiHandler.sendRequestWithResponse(message)
//
//            is AiModels.OpenAIModels,
//                -> openAIApiHandler.sendRequestWithResponse(message)
//        }

        val aiMessage = MessageAI(role = "assistant", content = message)

        chatAiChatFlow.update { chatAiChatFlow.value.add(aiMessage) }

    }

    private val currentModel: AiModels = AiModels.GroqModels.BASE_IMAGE_MODEL


}

interface AiModels {

    val value: String

    enum class GroqModels(override val value: String) : AiModels {
        BASE_IMAGE_MODEL("llama-3.2-11b-vision-preview"),
        TEXT_MODEL("llama3-groq-8b-8192-tool-use-preview"),
    }

    enum class GeminiModels(override val value: String) : AiModels {
        BASE_MODEL("gemini-1.5-flash"),
        GEMINI_IMAGE("")
    }

    enum class OpenAIModels(override val value: String) : AiModels {
        BASE_MODEL(""),
        OPENAI_IMAGE("")
    }

}