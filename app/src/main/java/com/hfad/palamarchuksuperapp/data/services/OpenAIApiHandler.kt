package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.data.dtos.OpenAIModelDTO
import com.hfad.palamarchuksuperapp.data.dtos.toOpenAIModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.LLMName
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
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
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OpenAIApiHandler @AssistedInject constructor(
    private val httpClient: HttpClient,
    @Assisted val initAiHandlerInfo: AiHandlerInfo,
) : AiModelHandler {

    private val _aiHandlerInfo: MutableStateFlow<AiHandlerInfo> =
        MutableStateFlow(initAiHandlerInfo)
    override val aiHandlerInfo: StateFlow<AiHandlerInfo> = _aiHandlerInfo.asStateFlow()

    override suspend fun getResponse(
        messageList: PersistentList<MessageGroup>,
    ): Result<MessageAI, AppError> {
        return safeApiCall {
            val gptRequest = messageList.toOpenAIRequest(model = initAiHandlerInfo.model)

            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${aiHandlerInfo.value.aiApiKey}")
                setBody(gptRequest)
            }

            if (response.status.value in 200..299) {
                val openAIResponse = response.body<ChatCompletionResponse>()
                val responseMessage = MessageAI(
                    message = openAIResponse.choices[0].message.content,
                    model = initAiHandlerInfo.model,
                    messageGroupId = 0 // Handler don't need to know message group
                )
                Result.Success(responseMessage)
            } else if (response.status.value in 401..599) {
                val openAiError = response.body<OpenAIError>()
                Result.Error(
                    AppError.NetworkException.ApiError.CustomApiError(openAiError.error.message),
                )
            } else {
                Result.Error(
                    error = AppError.NetworkException.ApiError.UndefinedError(
                        message = "Unknown error, please connect developer."
                    )
                )
            }
        }
    }

    override suspend fun getModels(): Result<List<AiModel.OpenAIModel>, AppError> {
        return Result.Success(
            listOf(
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o-mini",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "o1",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o-realtime-preview",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4-turbo",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-3.5-turbo",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "dall-e-3",
                    isSupported = true
                ),
                OpenAIModelDTO(
                    llmName = LLMName.OPENAI,
                    modelName = "dall-e-2",
                    isSupported = true
                ),
            ).map {
                it.toOpenAIModel()
            }
        )
    }

    override fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo) {
        _aiHandlerInfo.update {
            aiHandlerInfo
        }
    }
}

fun PersistentList<MessageGroup>.toOpenAIRequest(model: AiModel): GptRequested {
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
    fun create(aiHandlerInfo: AiHandlerInfo): OpenAIApiHandler
}