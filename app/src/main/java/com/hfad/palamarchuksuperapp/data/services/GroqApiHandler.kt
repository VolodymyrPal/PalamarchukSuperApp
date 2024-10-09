package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import coil.network.HttpException
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.domain.models.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) {
    private val apiKey = BuildConfig.GROQ_KEY
    private val max_tokens: Int = 100
    private val adminRoleMessage: Message = GroqContentBuilder.Builder().let {
        it.role = "system"
        it.text("You are tutor and trying to solve users problem on image")
    }.buildChat()

    val errorFlow = MutableStateFlow<DataError?>(null)

    val chatHistory: MutableStateFlow<List<Message>> =
        MutableStateFlow(emptyList())
//    MutableStateFlow(mutableListOf(adminRoleMessage))

    private val url = "https://api.groq.com/openai/v1/chat/completions"


    suspend fun getRespondChatImage(message: Message) {
        try {
            chatHistory.update { chatHistory.value.plus(message) }
            Log.d("Groq message: ", chatHistory.value.toString())

            val requestBody = Json.encodeToString(
                value = GroqRequest(
                    model = Models.GROQ_IMAGE.value,
                    messages = chatHistory.value
                )
            )

            val request = httpClient.post(url) {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(
                    GroqRequest(
                        model = Models.GROQ_SIMPLE_TEXT.value,
                        messages = chatHistory.value
                    )
                )
            }
            if (request.status == HttpStatusCode.OK) {
                val response = request.body<ChatCompletionResponse>()

                val responseMessage = GroqContentBuilder.Builder().let {
                    it.role = "assistant"
                    it.buildText((response.choices[0].message as MessageText).content)
                }
                Log.d("Groq response:", request.body<String>())

                chatHistory.update {
                    chatHistory.value.plus(responseMessage)
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
                                DataError.Network.BadRequest
                            }

                            401 -> {
                                DataError.Network.Unauthorized
                            }

                            403 -> {
                                DataError.Network.Forbidden
                            }

                            in 400..500 -> {
                                DataError.Network.InternalServerError
                            }

                            in 500..600 -> {
                                DataError.Network.Unknown
                            }

                            else -> {
                                DataError.Network.Unknown
                            }
                        }
                    }

                    else -> {
                        DataError.Network.Unknown
                    }
                }
            )
        }
    }
}


class GroqContentBuilder {

    class Builder {

        private var contents: MutableList<GroqContentType> = arrayListOf()

        var role = "user"

        @JvmName("addPart")
        fun <T : GroqContentType> content(data: T) = apply { contents.add(data) }

        @JvmName("addText")
        fun text(text: String) = content(ContentText(text = text))

        @JvmName("addImage")
        fun image(image: Base64) = content(ContentImage(image_url = ImageUrl(image)))

        //fun build(): MessageText = MessageText(content = contents, role = role)
        fun buildChat(): MessageChat = MessageChat(content = contents, role = role)

        fun buildText(request: String): MessageText = MessageText(content = request, role = role)
    }
}

class CodeError(val value: Int) : Exception()

enum class Models(val value: String) {
    GROQ_SIMPLE_TEXT("llama3-8b-8192"),
    GROQ_IMAGE("llama-3.2-11b-vision-preview"),
}