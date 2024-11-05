package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
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

class OpenAIApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) : AiModelHandler {

    private val openAiKey = BuildConfig.OPEN_AI_KEY_USER

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModel,
    ): Result<MessageAI, AppError> {
        val gptRequest = GptRequested(
            model = model.modelName,
            messages = messageList.toOpenAIRequest()
        )

        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $openAiKey")
                setBody(gptRequest)
            }

            if (response.status == HttpStatusCode.OK) {
                val openAIResponse = response.body<String>()
//                val responseMessage = MessageAI(
//                    role = Role.MODEL,
//                    content = openAIResponse.choices.firstOrNull()?.message?.content
//                        ?: "No response"
//                )
                Result.Success(MessageAI(role = Role.MODEL, content = openAIResponse))
            } else {
                Result.Error(AppError.Network.RequestError.BadRequest)
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun getModels(): Result<List<AiModel>, AppError> {
        // Заглушка, так как OpenAI API не предоставляет endpoint для получения списка моделей в данном контексте
        return Result.Success(emptyList())
    }


    private val imageMessageRequest = ImageMessageRequest(
        imageUrl = ImageRequest("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
    )

    private val systemMessageRequest = TextMessageRequest(
        text = "Ты учитель, который называет меня Угольком. Помоги мне с изображением. Отвечай на русском."
    )

    private val systemRequest = RequestRole(
        role = "system",
        content = listOf(systemMessageRequest)
    )

    private val roleRequest = RequestRole(
        role = "user",
        content = listOf(imageMessageRequest)
    )

    private val gptRequest = GptRequested(
        messages = listOf(systemRequest, roleRequest)
    )

    private val jsonRequest = Json.encodeToString(gptRequest)

    suspend fun sendRequestWithResponse(): String {
        return try {
            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $openAiKey")
                setBody(jsonRequest)
            }
            response.body<String>()
        } catch (e: Exception) {
            e.message ?: "Error"
        }
    }
}

fun PersistentList<MessageAI>.toOpenAIRequest(): List<RequestRole> {
    return map { message ->
        RequestRole(
            role = message.role.value,
            content = listOf(
                when (message.type) {
                    MessageType.TEXT -> TextMessageRequest(text = message.content)
                    MessageType.IMAGE -> ImageMessageRequest(
                        imageUrl = ImageRequest(url = message.content)
                    )
                }
            )
        )
    }
}
