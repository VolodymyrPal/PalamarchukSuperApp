package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GeminiApiHandler @Inject constructor(private val httpClient: HttpClient) {
    private val apiKey = BuildConfig.GEMINI_AI_KEY
    private val url =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"


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
}