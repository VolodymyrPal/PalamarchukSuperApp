package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.LLMName
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageAiContent
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import com.hfad.palamarchuksuperapp.data.entities.AiModel.OpenAIModel
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
        messageList: PersistentList<MessageAI>,
    ): Result<SubMessageAI, AppError> {
        val gptRequest = messageList.toOpenAIRequest(model = initAiHandlerInfo.model)

        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${aiHandlerInfo.value.aiApiKey}")
                setBody(gptRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val openAIResponse = response.body<ChatCompletionResponse>()
                val responseMessage = SubMessageAI(
                    message = openAIResponse.choices[0].message.content,
                    model = initAiHandlerInfo.model
                )
                Result.Success(responseMessage)
            } else if (response.status.value in 401..599) {
                val openAiError = response.body<OpenAIError>()
                Result.Error(AppError.CustomError(openAiError.error.message),
                    data = SubMessageAI(
                        model = initAiHandlerInfo.model
                    ))
            } else {
                throw CodeError(response.status.value)
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun getModels(): Result<List<AiModel>, AppError> {
        return Result.Success(
            listOf(
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o-mini",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "o1",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4o-realtime-preview",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4-turbo",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-4",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "gpt-3.5-turbo",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "dall-e-3",
                    isSupported = true
                ),
                OpenAIModel(
                    llmName = LLMName.OPENAI,
                    modelName = "dall-e-2",
                    isSupported = true
                ),


            )
        )
    }

    override fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo) {
        _aiHandlerInfo.update {
            aiHandlerInfo
        }
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
    fun create(aiHandlerInfo: AiHandlerInfo): OpenAIApiHandler
}