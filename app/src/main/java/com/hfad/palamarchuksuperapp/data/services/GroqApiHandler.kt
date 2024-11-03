package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) : AiModelHandler {
    private val apiKey = BuildConfig.GROQ_KEY

    //    private val max_tokens = 1024
//    private val adminRoleMessage: Message = GroqContentBuilder.Builder().let {
//        it.role = "system"
//        it.buildText(
//            "You are math tutor. User send you image with sample. " +
//                    "Please provide answer and solve this sample."
//        )
//    }

    private val url = "https://api.groq.com/openai/v1/chat/completions"

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModel,
    ): Result<MessageAI, AppError> {

        val request = httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                messageList.toGroqRequest(model)
            )
        }
        try {
            if (request.status == HttpStatusCode.OK) {
                val response = request.body<ChatCompletionResponse>()
                val responseMessage = MessageAI(
                    content = (response.choices[0].message as MessageText).content,
                    role = Role.MODEL
                )
                return Result.Success(responseMessage)
            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
            return Result.Error(handleException(e))
        }
    }

    override suspend fun getModels(): Result<List<AiModel>, AppError> {
        val response = httpClient.get(
            "https://api.groq.com/openai/v1/models"
        ) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
        }
        return if (response.status == HttpStatusCode.OK) {
            val list = response.body<AiModel.GroqModelList>()
            return Result.Success(list.data)
        } else {
            Result.Error(AppError.Network.RequestError.BadRequest)
        }
    }
}


class GroqContentBuilder {

    class Builder {

        private var messages: MutableList<Message> = arrayListOf()

        fun addMessage(request: String, role: String) {
            val message = MessageText(content = request, role = role)
            messages.add(message)
        }

        fun buildChatRequest(model: AiModel): GroqRequest = GroqRequest(
            messages = messages,
            model = model.modelName,
            maxTokens = 1024
        )

        @JvmName("addText")
        private fun text(text: String) = ContentText(text = text)

        @JvmName("addImage")
        private fun image(image: Base64) = ContentImage(
            image_url =
            ImageUrl("data:image/jpeg;base64,$image")
        )

        @JvmName("addImageText")
        fun imageText(image: Base64, text: String, role: String) {
            (messages.removeIf { it is MessageChat }) // TODO Groq supports only one image
            messages.add(
                MessageChat(
                    role = role,
                    content = listOf(
                        text(text),
                        image(image)
                    )
                )
            )
        }

        //TODO add image content
//        private var imageContent: MutableList<GroqContentType> = arrayListOf()
//        @JvmName("addPart")
//        fun <T : GroqContentType> content(data: T) = apply { imageContent.add(data) }
//
//
//        fun buildText(request: String, role: String = Role.USER.value): MessageText {
//            return MessageText(content = request, role = role)
//        }
    }
}

fun List<MessageAI>.toGroqRequest(model: AiModel = AiModel.GroqModels.BASE_MODEL): GroqRequest {
    val groqRequest = GroqContentBuilder.Builder().also { builder ->
        for (message in this) {
            when (message.type) {
                MessageType.TEXT -> {
                    builder.addMessage(
                        request = message.content,
                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
                    )
                }

                MessageType.IMAGE -> {
                    builder.imageText(
                        image = if (message.otherContent is Base64) message.otherContent else "",
                        text = message.content,
                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
                    )
                }
            }
        }
    }.buildChatRequest(model)

    return groqRequest
}

class CodeError(val value: Int) : Exception()