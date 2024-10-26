package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.repository.AiModels
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
import retrofit2.HttpException

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) : AiModelHandler {
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private fun getUrl(model: AiModels = AiModels.GeminiModels.BASE_MODEL, key: String = apiKey) =
        "https://generativelanguage.googleapis.com/v1beta/models/${model.modelName}:generateContent?key=$key"

    suspend fun getAvailableModels(): List<AiModels.GeminiModel> {
        val response =
            httpClient.get("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey") {
            }
        val list =  response.body<GeminiModelsResponse>()
        return list.models
    }

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels?,
    ): Result<MessageAI, AppError> {
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
                    Result.Error(AppError.Network.Unknown)
                }

                is CodeError -> {
                    when (e.value) {
                        400 -> {
                            Result.Error(AppError.Network.BadRequest)
                        }

                        401 -> {
                            Result.Error(AppError.Network.Unauthorized)
                        }

                        403 -> {
                            Result.Error(AppError.Network.Forbidden)
                        }

                        in 400..500 -> {
                            Result.Error(AppError.CustomError("Ошибка запроса"))
                        }

                        in 500..600 -> {
                            Result.Error(AppError.CustomError("Ошибка сервера"))
                        }

                        else -> {
                            Result.Error(AppError.CustomError("Неизвестная ошибка"))
                        }
                    }
                }

                else -> {
                    Result.Error(AppError.CustomError(error = e))
                }
            }
        }
    }
}