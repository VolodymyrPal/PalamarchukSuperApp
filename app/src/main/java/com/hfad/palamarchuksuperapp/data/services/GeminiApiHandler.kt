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
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private val url =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    val requestList: MutableList<GeminiRequest> = mutableListOf()

    suspend fun simpleTextRequest(text: String): String {
        val part = TextPart(text = text)
        val geminiContent = GeminiContent(listOf(part))
        val geminiRequest = GeminiRequest(listOf(geminiContent))

        return try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(geminiRequest)
            }
            response.body<String>()
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }

    suspend fun sendRequestWithResponse(geminiRequest: GeminiRequest): String {
        try {
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(geminiRequest)
                }
            val textResponse = response.body<GeminiResponse>().candidates[0].content.parts[0].text
//            if (response.status == HttpStatusCode.OK) {
//                requestList.add(geminiRequest)
//                //response.body<GeminiRequest>().let { requestList.add(it) }
//                requestList.add(GeminiRequest(listOf(GeminiContent(listOf(TextPart(textResponse)), role = "model"))))
//            }
//
//            val request = GeminiContentBuilder.Builder()
//                .text("What was my previous questions?")
//                .build()
//
//            requestList.add(request)
//
//            val responseTwo = httpClient.post(url) {
//                contentType(ContentType.Application.Json)
//                for (i in requestList) {
//                    setBody(i)
//                }
//            }
//            val textResponse2 = responseTwo.body<GeminiResponse>().candidates[0].content.parts[0].text
//            Log.d("My list: ", "$requestList")
            return textResponse
        } catch (e: Exception) {
            return e.message ?: "Error"
        }
    }
}

class GeminiContentBuilder {

    class Builder {

        private var parts: MutableList<Part> = arrayListOf()

        @JvmName("addPart")
        fun <T : Part> part(data: T) = apply { parts.add(data) }

        @JvmName("addText")
        fun text(text: String) = part(TextPart(text))

        @JvmName("addImage")
        fun image(image: Base64) = part(ImagePart(InlineData(data = image)))

        fun build(): GeminiRequest = GeminiRequest(listOf(GeminiContent(parts)))
    }
}