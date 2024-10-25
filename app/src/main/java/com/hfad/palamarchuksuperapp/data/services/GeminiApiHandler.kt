package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.repository.AiModels
import com.hfad.palamarchuksuperapp.domain.models.DataError
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
import retrofit2.HttpException

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) : AiModelHandler {
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private fun getUrl(model: AiModels = AiModels.GeminiModels.BASE_MODEL, key: String = apiKey) =
        "https://generativelanguage.googleapis.com/v1beta/models/${model.value}:generateContent?key=$key"

    suspend fun getAvailableModels(): List<AiModels.GeminiModels> {
        val response = httpClient.post(getUrl()) {
            contentType(ContentType.Application.Json)
            setBody(
                "https://generativelanguage.googleapis.com/v1beta/models/" +
                        "${AiModels.GeminiModels.BASE_MODEL}?key=$apiKey"
            )
        }
        return listOf(
            AiModels.GeminiModels.BASE_MODEL
        )
    }

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels?,
    ): Result<MessageAI, DataError> {
        try {
            val request =
                httpClient.post(getUrl(model = model ?: AiModels.GeminiModels.BASE_MODEL)) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        messageList.toGeminiRequest(
                            model = model ?: AiModels.GeminiModels.BASE_MODEL
                        )
                    )
                }

            if (request.status == HttpStatusCode.OK) {
                val response = request.body<GeminiResponse>()
                val responseMessage = MessageAI(
                    role = "model",
                    content = response.candidates[0].content.parts[0].text,
                )
                return Result.Success(responseMessage)
            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
            return when (e) {
                is HttpException -> {
                    Result.Error(DataError.Network.Unknown)
                }

                is CodeError -> {
                    when (e.value) {
                        400 -> {
                            Result.Error(DataError.Network.BadRequest)
                        }

                        401 -> {
                            Result.Error(DataError.Network.Unauthorized)
                        }

                        403 -> {
                            Result.Error(DataError.Network.Forbidden)
                        }

                        in 400..500 -> {
                            Result.Error(DataError.CustomError("Ошибка запроса"))
                        }

                        in 500..600 -> {
                            Result.Error(DataError.CustomError("Ошибка сервера"))
                        }

                        else -> {
                            Result.Error(DataError.CustomError("Неизвестная ошибка"))
                        }
                    }
                }

                else -> {
                    Result.Error(DataError.CustomError(error = e))
                }
            }
        }
    }
}