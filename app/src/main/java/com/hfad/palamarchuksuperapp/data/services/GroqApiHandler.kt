package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import com.hfad.palamarchuksuperapp.BuildConfig
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

    val chatHistory: MutableStateFlow<List<Message>> =
        MutableStateFlow(emptyList())
//    MutableStateFlow(mutableListOf(adminRoleMessage))

    val url = "https://api.groq.com/openai/v1/chat/completions"
    val url_image = "https://api.groq.com/openai/v1/chat/generate"


    suspend fun sendMessageChatImage(message: Message) {
        chatHistory.update {
            chatHistory.value.plus(message)
        }
        val requestBody = Json.encodeToString(
            value = GroqRequest(
                model = Models.GROQ_IMAGE.value,
                messages = chatHistory.value
            )
        )
        Log.d("Request Body", requestBody) // Логируем тело запроса

        val request = httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(GroqRequest(model = Models.GROQ_SIMPLE_TEXT.value, messages = chatHistory.value))
        }
        Log.d("Request", request.body<String>())
        if (request.status == HttpStatusCode.OK) {
            val response = request.body<ChatCompletionResponse>()

            val responseMessage = GroqContentBuilder.Builder().let {
                it.role = "assistant"
                it.buildText("${(response.choices[0].message as MessageText).content}")
            }
            chatHistory.update {
                chatHistory.value.plus(responseMessage)
            }
        } else if (request.status != HttpStatusCode.BadRequest) {
            Log.d("TAG", "error in text ${request.status}")
        } else if (request.status.value > 400 || request.status.value < 500) {
            Log.d("TAG", "request error ${request.status}")
        } else if (request.status.value > 500 || request.status.value < 600) {
            Log.d("TAG", "server error ${request.status}")
        } else {
            Log.d("TAG", "unknown error ${request.status}")
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
        fun text(text: String) = content(ContentText(text))

        @JvmName("addImage")
        fun image(image: Base64) = content(ContentImage(image_url = ImageUrl(image)))

//        fun build(): MessageText = MessageText(content = contents, role = role)
        fun buildChat(): MessageChat = MessageChat(content = contents, role = role)

        fun buildText(request: String): MessageText = MessageText(content = request, role = role)
    }
}


enum class Models(val value: String) {
    GROQ_SIMPLE_TEXT("llama3-8b-8192"),
    GROQ_IMAGE("llava-v1.5-7b-4096-preview"),
}