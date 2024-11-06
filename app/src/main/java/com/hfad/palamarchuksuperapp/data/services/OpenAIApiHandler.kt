package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject

class OpenAIApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) : AiModelHandler {

    private val openAiKey = BuildConfig.OPEN_AI_KEY_USER

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModel,
    ): Result<MessageAI, AppError> {
        val gptRequest = messageList.toOpenAIRequest(model = model)

        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $openAiKey")
                setBody(gptRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val openAIResponse = response.body<ChatCompletionResponse>()
                val responseMessage = MessageAI(
                    role = Role.MODEL,
                    content = openAIResponse.choices[0].message.content
                )
                Result.Success(responseMessage)
            } else {
                Result.Error(AppError.Network.RequestError.BadRequest)
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun getModels(): Result<List<AiModel>, AppError> {
        // Заглушка, так как OpenAI API не предоставляет endpoint для получения списка моделей в данном контексте
        return Result.Success(emptyList())
    }
}

fun PersistentList<MessageAI>.toOpenAIRequest(model: AiModel): GptRequested { // List<RequestRole> {
    return GptRequested(
        model = model.modelName,
        messages = this.map { message ->
            RequestRole(
                role = when (message.role) {
                    Role.USER -> "user"
                    Role.MODEL -> "assistant"
                    Role.PROMPT -> "system"
                    Role.ASSISTANT -> "assistant" // OpenAI API не поддерживает такую роль TODO скорректировать
                },
                content = listOf(
                    when (message.type) {
                        MessageType.TEXT -> TextMessageRequest(text = message.content)
                        MessageType.IMAGE -> ImageMessageRequest(imageUrl = ImageRequest(url = message.content))
                    }
                )
            )
        }
    )
}
