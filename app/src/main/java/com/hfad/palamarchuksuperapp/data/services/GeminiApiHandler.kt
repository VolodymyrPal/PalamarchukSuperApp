package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.data.dtos.toGeminiModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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

class GeminiApiHandler @AssistedInject constructor(
    private val httpClient: HttpClient,
    @Assisted val initAiHandlerInfo: AiHandlerInfo,
) : AiModelHandler {

    private val _aiHandlerInfo: MutableStateFlow<AiHandlerInfo> =
        MutableStateFlow(initAiHandlerInfo)
    override val aiHandlerInfo: StateFlow<AiHandlerInfo> = _aiHandlerInfo.asStateFlow()

    private fun getUrl(
        model: AiModel = aiHandlerInfo.value.model,
        key: String = aiHandlerInfo.value.aiApiKey,
    ) =
        "https://generativelanguage.googleapis.com/v1beta/models/${model.modelName}:generateContent?key=$key"


    override suspend fun getModels(): AppResult<List<AiModel.GeminiModel>, AppError> {
        return safeApiCall {
            val response = httpClient.get(
                "https://generativelanguage.googleapis.com/v1beta/models?key=${aiHandlerInfo.value.aiApiKey}"
            )
            if (response.status.value in 200..299) {
                val list = response.body<GeminiModelsResponse>()
                AppResult.Success(list.models.map { it.toGeminiModel() })
            } else {
                AppResult.Error(AppError.NetworkException.ApiError.NotFound())
            }
        }
    }

    override fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo) {
        _aiHandlerInfo.update { aiHandlerInfo }
    }

    override suspend fun getResponse(
        messageList: PersistentList<MessageGroup>,
    ): AppResult<MessageAI, AppError> {
        return safeApiCall {
            val request =
                httpClient.post(getUrl(model = initAiHandlerInfo.model)) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        messageList.toGeminiRequest(
                        )
                    )
                }

            if (request.status == HttpStatusCode.OK) {
                val response = request.body<GeminiResponse>()
                val responseMessage = MessageAI(
                    message = response.candidates[0].content.parts[0].text,
                    model = initAiHandlerInfo.model,
                    messageGroupId = 0 // Handler don't need to know message group
                )
                AppResult.Success(responseMessage)
            } else if (request.status.value in 400..599) {
                val geminiError = request.body<GeminiError>()
                AppResult.Error(
                    error = AppError.NetworkException.ApiError.CustomApiError(
                        geminiError.error.message
                    )
                )
            } else {
                AppResult.Error(
                    error = AppError.NetworkException.ApiError.UndefinedError(
                        message = "Unknown error, please connect developer."
                    )
                )
            }
        }
    }
}


fun List<MessageGroup>.toGeminiRequest(): GeminiRequest {
    val geminiRequest = GeminiBuilder.RequestBuilder().also { builder ->
        for (message in this) {
            when (message.type) {
                MessageType.TEXT -> {
                    builder.contentText(
                        role = message.role.value,
                        content = message.content.firstOrNull { it.isChosen }?.message
                            ?: message.content.first().message
                    )
                }

                MessageType.IMAGE -> {
                    builder.contentImage(
                        role = message.role.value,
                        content = if (message.content.first().otherContent is MessageAiContent.Image)
                            (message.content.first().otherContent as MessageAiContent.Image).image
                        else "Unsupported content"
                    )
                }
            }
        }
    }.buildChatRequest()

    return geminiRequest
}

@AssistedFactory
interface GeminiAIApiHandlerFactory {
    fun create(aiHandlerInfo: AiHandlerInfo): GeminiApiHandler
}