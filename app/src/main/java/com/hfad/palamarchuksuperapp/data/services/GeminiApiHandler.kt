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

            return if (request.status == HttpStatusCode.OK) {
                val response = request.body<GeminiResponse>()
                val responseMessage = MessageAI(
                    role = "model",
                    content = response.candidates[0].content.parts[0].text,
                )
                Result.Success(responseMessage)
            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
//            errorFlow.emit(
//                when (e) {
//                    is HttpException -> {
//                        DataError.Network.InternalServerError
//                    }
//
//                    is CodeError -> {
//                        when (e.value) {
//                            400 -> {
//                                //DataError.Network.BadRequest
//                                DataError.CustomError(e.value.toString())
//                            }
//
//                            401 -> {
//                                DataError.CustomError(e.value.toString())
//                                //DataError.Network.Unauthorized
//                            }
//
//                            403 -> {
//                                //DataError.Network.Forbidden
//                                DataError.CustomError(e.value.toString())
//
//                            }
//
//                            in 400..500 -> {
//                                DataError.CustomError(e.value.toString())
//                                //DataError.Network.InternalServerError
//                            }
//
//                            in 500..600 -> {
//                                DataError.CustomError(e.value.toString())
//                                //DataError.Network.Unknown
//                            }
//
//                            else -> {
//                                DataError.CustomError(e.value.toString())
//                                //DataError.Network.Unknown
//                            }
//                        }
//                    }
//
//                    else -> {
//                        DataError.CustomError(e.toString())
//                    }
//                }
//            )
        }

        return Result.Success(MessageAI("", ""))


    }
}