package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageAiContent
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result
import io.ktor.client.request.get

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) : AiModelHandler {
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private fun getUrl(model: AiModel = AiModel.GeminiModels.BASE_MODEL, key: String = apiKey) =
        "https://generativelanguage.googleapis.com/v1beta/${model.modelName}:generateContent?key=$key"

    override val baseModel = AiModel.GeminiModels.BASE_MODEL


    override suspend fun getModels(): Result<List<AiModel.GeminiModel>, AppError> {

        val response = httpClient.get(
            "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
        )
        return if (response.status == HttpStatusCode.OK) {
            val list = response.body<GeminiModelsResponse>()
            return Result.Success(list.models)
        } else {
            Result.Error(AppError.Network.RequestError.BadRequest)
        }
    }

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModel?,
    ): Result<SubMessageAI, AppError> {
        try {
            val request =
                httpClient.post(getUrl(model = model?: baseModel)) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        messageList.toGeminiRequest(
                        )
                    )
                }

            if (request.status == HttpStatusCode.OK) {
                val response = request.body<GeminiResponse>()
                val responseMessage = SubMessageAI(
                    message = response.candidates[0].content.parts[0].text,
                    model = model?: baseModel
                )
                return Result.Success(responseMessage)
            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
            return Result.Error(handleException(e))
        }
    }
}

fun handleException(e: Exception): AppError {
    return when (e) {
        is CodeError -> {
            when (e.value) {
                400 -> AppError.Network.RequestError.BadRequest
                401 -> AppError.Network.RequestError.Unauthorized
                403 -> AppError.Network.RequestError.Forbidden
                in 400..500 -> AppError.CustomError("Ошибка запроса")
                in 500..600 -> AppError.CustomError("Ошибка сервера")
                else -> AppError.CustomError("Неизвестная ошибка")
            }
        }

        else -> AppError.CustomError(error = e)
    }
}

fun List<MessageAI>.toGeminiRequest(): GeminiRequest {
    val geminiRequest = GeminiBuilder.RequestBuilder().also { builder ->
        for (message in this) {
            when (message.type) {
                MessageType.TEXT -> {
                    builder.contentText(
                        role = message.role.value,
                        content = message.content.first().message
                    )
                }

                MessageType.IMAGE -> {
                    builder.contentImage(
                        role = message.role.value,
                        content = (message.content.first().otherContent as MessageAiContent.Image).image
                    )
                }
            }
        }
    }.buildChatRequest()

    return geminiRequest
}