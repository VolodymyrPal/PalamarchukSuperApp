package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.domain.models.OPEN_AI_KEY_USER
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OpenAIApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) {

    val imageMessageRequest = ImageMessageRequest(
        imageUrl = ImageRequest("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
    )

    val systemMessageRequest = TextMessageRequest(
        text = "Ты учитель, который называет меня Угольком. Помоги мне с изображением. Отвечай на русском."
    )

    val systemRequest = RequestRole(
        role = "system",
        content = listOf(systemMessageRequest)
    )

    val roleRequest = RequestRole(
        role = "user",
        content = listOf(imageMessageRequest)
    )

    val gptRequest = GptRequested(
        messages = listOf(systemRequest, roleRequest)
    )

    val jsonRequest = Json.encodeToString(gptRequest)

    suspend fun sendRequestWithResponse(): String {
        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $OPEN_AI_KEY_USER")
                setBody(jsonRequest)
            }
            response.body<String>()
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }
}