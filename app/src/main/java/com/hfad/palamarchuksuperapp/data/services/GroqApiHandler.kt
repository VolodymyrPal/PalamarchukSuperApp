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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
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

        val listToPass = if (messageList.last().type == MessageType.IMAGE) {
            messageList.last().toGroqRequest(model)
        } else {
            messageList.toGroqRequest(model)
        }

        val request = httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                listToPass
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
        fun imageWithText(image: Base64, text: String, role: String) {
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
    }
}

fun MessageAI.toGroqRequest(model: AiModel): GroqRequest {
    val builder = GroqContentBuilder.Builder().also {
        when (this.type) {
            MessageType.IMAGE -> {
                it.imageWithText(
                    image = if (this.otherContent is Base64) this.otherContent else "",
                    text = this.content,
                    role = if (this.role == Role.MODEL) "assistant" else this.role.value
                )
            }

            MessageType.TEXT -> {
                it.addMessage(
                    request = this.content,
                    role = if (this.role == Role.MODEL) "assistant" else this.role.value
                )
            }
        }
    }.buildChatRequest(model)
    return builder
}

/*
********
TODO GROQ: currently groq can handle only one image at a time. Find better solution in future.
********
 */

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
                    builder.addMessage(
                        request = message.content,
                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
                    )
                }
            }
        }
    }.buildChatRequest(model)

    return groqRequest
}

class CodeError(val value: Int) : Exception()