package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) {
    private val part = Part(text = "Is my request completed?")
    private val geminiContent = GeminiContent(listOf(part))
    private val geminiRequest = GeminiRequest(listOf(geminiContent))

    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private val url =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun sendRequestWithResponse(): String {
        return try {
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(geminiRequest)
                }
            response.body<String>()
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }
}

class GeminiContentBuilder (val parts: List<Part>) {

    class Builder {

        var parts: MutableList<Part> = arrayListOf()

        @JvmName("addPart") fun <T : Part> part(data: T) = apply { parts.add(data) }

        @JvmName("addText") fun text(text: String) = part(TextPart(text))

        @JvmName("addImage") fun image(image: Base64) = part(ImagePart(InlineData(data = image)))

        fun build(): GeminiRequest = GeminiRequest(listOf(GeminiContent(parts)))
    }
}