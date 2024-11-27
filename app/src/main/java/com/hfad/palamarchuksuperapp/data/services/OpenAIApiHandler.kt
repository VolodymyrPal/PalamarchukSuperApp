package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageAiContent
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AiHandler
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList

class OpenAIApiHandler @AssistedInject constructor(
    private val httpClient: HttpClient,
    @Assisted override val aiHandler: AiHandler
) : AiModelHandler {

    private val openAiKey = BuildConfig.OPEN_AI_KEY_USER

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
    ): Result<SubMessageAI, AppError> {
        val gptRequest = messageList.toOpenAIRequest(model = aiHandler.model)

        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $openAiKey")
                setBody(gptRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val openAIResponse = response.body<ChatCompletionResponse>()
                val responseMessage = SubMessageAI(
                    message = openAIResponse.choices[0].message.content,
                    model = aiHandler.model
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
        // Заглушка, так как OpenAI API не предоставляет endpoint для получения списка моделей
        return Result.Success(emptyList())
    }
}

fun PersistentList<MessageAI>.toOpenAIRequest(model: AiModel): GptRequested {
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
                        MessageType.TEXT -> TextMessageRequest(
                            text = message.content.firstOrNull { it.isChosen }?.message
                                ?: message.content.first().message
                        )

                        MessageType.IMAGE -> ImageMessageRequest(
                            imageUrl =
                                ImageRequest(
                                    url = if (message.content.first().otherContent is MessageAiContent.Image)
                                        (message.content.first().otherContent as MessageAiContent.Image).image
                                    else {
                                        "Unsupported image type"
                                    }
                                )
                        )
                    }
                )
            )
        }
    )
}

@AssistedFactory
interface OpenAIApiHandlerFactory {
    fun create(aiHandler: AiHandler): OpenAIApiHandler
}