package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import coil.network.HttpException
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.repository.AiModels
import com.hfad.palamarchuksuperapp.domain.models.DataError
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) : AiModelHandler {
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private fun getUrl(model: AiModels = AiModels.GeminiModels.BASE_MODEL, key: String = apiKey) =
        "https://generativelanguage.googleapis.com/v1beta/models/${model.value}:generateContent?key=$key"

    suspend fun simpleTextRequest(text: String): String {
        val part = TextPart(text = text)
        val geminiContent = GeminiContent(listOf(part))
        val geminiRequest = GeminiRequest(listOf(geminiContent))

        return try {
            val response = httpClient.post(getUrl()) {
                contentType(ContentType.Application.Json)
                setBody(geminiRequest)
            }
            response.body<String>()
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }

    suspend fun sendRequestWithResponse(geminiRequest: GeminiRequest): MessageAI {
        try {
            Log.d("Request: ", Json.encodeToString(geminiRequest))
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(geminiRequest)
                }
            Log.d("Get response: ", "${response.body<String>()}")
            val textResponse = response.body<GeminiResponse>().candidates[0].content.parts[0].text
            if (response.status == HttpStatusCode.OK) {
                return MessageAI(role = "model", content = textResponse, type = MessageType.TEXT)
            } else { // TODO better handling request
                return MessageAI(role = "system", content = "error", type = MessageType.TEXT)
            }
        } catch (e: Exception) {
            return MessageAI(e.message ?: "Error")
        }
    }



    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels?,
    ): MessageAI {

        val request = httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                messageList.toGeminiRequest(model = model ?: AiModels.GeminiModels.BASE_MODEL)
            )
        }
        try {

            Log.d("Groq response:", request.body<String>())

            if (request.status == HttpStatusCode.OK) {

                val response = request.body<ChatCompletionResponse>()

                val responseMessage = GroqContentBuilder.Builder().let {
                    it.role = "assistant"
                    it.buildText((response.choices[0].message as MessageText).content)
                }

            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
            errorFlow.emit(
                when (e) {
                    is HttpException -> {
                        DataError.Network.InternalServerError
                    }

                    is CodeError -> {
                        when (e.value) {
                            400 -> {
                                //DataError.Network.BadRequest
                                DataError.CustomError(e.value.toString())
                            }

                            401 -> {
                                DataError.CustomError(e.value.toString())
                                //DataError.Network.Unauthorized
                            }

                            403 -> {
                                //DataError.Network.Forbidden
                                DataError.CustomError(e.value.toString())

                            }

                            in 400..500 -> {
                                DataError.CustomError(e.value.toString())
                                //DataError.Network.InternalServerError
                            }

                            in 500..600 -> {
                                DataError.CustomError(e.value.toString())
                                //DataError.Network.Unknown
                            }

                            else -> {
                                DataError.CustomError(e.value.toString())
                                //DataError.Network.Unknown
                            }
                        }
                    }

                    else -> {
                        DataError.CustomError(e.toString())
                    }
                }
            )
        }

        return MessageAI("", "")


    }
}