package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import coil.network.HttpException
import com.hfad.palamarchuksuperapp.BuildConfig
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) : AiModelHandler {
    private val apiKey = BuildConfig.GROQ_KEY
    private val max_tokens = 1024
    private val adminRoleMessage: Message = GroqContentBuilder.Builder().let {
        it.role = "system"
        it.buildText("You are math tutor. User send you image with sample. " +
                "Please provide answer and solve this sample.")
    }
    val errorFlow = MutableStateFlow<DataError?>(null)

    val chatHistory: MutableStateFlow<PersistentList<Message>> =
        MutableStateFlow(persistentListOf())
        //MutableStateFlow(mutableListOf(adminRoleMessage))

    private val url = "https://api.groq.com/openai/v1/chat/completions"

    override suspend fun getResponse(messageList: PersistentList<MessageAI>): MessageAI {
        return MessageAI("","")
    }


    suspend fun getRespondChatOrImage(message: Message) {
        try {
            chatHistory.update { chatHistory.value.add(message) }
            Log.d("Groq message: ", chatHistory.value.toString())

            val requestBody = Json.encodeToString(
                value = GroqRequest(
                    model = Models.GROQ_IMAGE.value,
                    messages = chatHistory.value,
                    maxTokens = max_tokens
                )
            )

            val request = httpClient.post(url) {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(
                    GroqRequest(
                        model = if (chatHistory.value.any { it is MessageChat })
                            Models.GROQ_IMAGE.value else Models.GROQ_SIMPLE_TEXT.value,
                        messages = chatHistory.value,
                        maxTokens = max_tokens
                    )
                )
            }
            Log.d("Groq response:", request.body<String>())

            if (request.status == HttpStatusCode.OK) {

                val response = request.body<ChatCompletionResponse>()

                val responseMessage = GroqContentBuilder.Builder().let {
                    it.role = "assistant"
                    it.buildText((response.choices[0].message as MessageText).content)
                }

                chatHistory.update { chatHistory.value.add(responseMessage) }
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
    GROQ_SIMPLE_TEXT("llama3-groq-8b-8192-tool-use-preview"),
    GROQ_IMAGE("llama-3.2-11b-vision-preview"),
}