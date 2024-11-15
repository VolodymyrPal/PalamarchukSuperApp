package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.AiModel
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.entities.MessageAiContent
import com.hfad.palamarchuksuperapp.data.entities.MessageType
import com.hfad.palamarchuksuperapp.data.entities.Role
import com.hfad.palamarchuksuperapp.data.entities.SubMessageAI
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
    override val chosen: Boolean = true
    override val enabled: Boolean = true
    override val baseModel = AiModel.GroqModels.BASE_MODEL

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
        model: AiModel?,
    ): Result<SubMessageAI, AppError> {

        val listToPass = if (messageList.last().type == MessageType.IMAGE) {
            messageList.last().toGroqRequest(model ?: baseModel)
        } else {
            messageList.toGroqRequest(model ?: baseModel)
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
                val response = request.body<GroqChatCompletionResponse>()
                val responseText = response.groqChoices[0].groqMessage
                val responseMessage = SubMessageAI(
                    message = if (responseText is GroqMessageText) responseText.content else "",
                    model = model ?: baseModel
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

        private var groqMessages: MutableList<GroqMessage> = arrayListOf()

        fun addMessage(request: String, role: String) {
            val message = GroqMessageText(content = request, role = role)
            groqMessages.add(message)
        }

        fun buildChatRequest(model: AiModel): GroqRequest = GroqRequest(
            groqMessages = groqMessages,
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
        fun imageWithText(image: Base64, request: String, role: String) {
            groqMessages.add(
                GroqMessageChat(
                    role = role,
                    content = listOf(
                        text(request),
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
                val image = this.content.first().otherContent
                it.imageWithText(
                    image = if (image is MessageAiContent.Image) image.image else "Unsupported content",
                    request = this.content.first().message,
                    role = if (this.role == Role.MODEL) "assistant" else this.role.value
                )
            }

            MessageType.TEXT -> {
                it.addMessage(
                    request = this.content.first().message,
                    role = if (this.role == Role.MODEL) "assistant" else this.role.value
                )
            }
        }
    }.buildChatRequest(model)
    return builder
}

/**
 *
 * TODO GROQ: currently groq can handle only one image at a time. Find better solution in future.
 *
 */

fun List<MessageAI>.toGroqRequest(model: AiModel = AiModel.GroqModels.BASE_MODEL): GroqRequest {
    val groqRequest = GroqContentBuilder.Builder().also { builder ->
        for (message in this) {
            when (message.type) {
                MessageType.TEXT -> {
                    builder.addMessage(
                        request = message.content.first().message,
                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
                    )
                }

                /** Groq currently can only handle one image at a time, so all images in list we convert to text */
                MessageType.IMAGE -> {
                    builder.addMessage(
                        request = message.content.first().message,
                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
                    )
                }
//                MessageType.IMAGE -> {
//                    builder.imageWithText(
//                        request = message.content,
//                        image = message.otherContent as Base64, //TODO For testing different models
//                        role = if (message.role == Role.MODEL) "assistant" else message.role.value
//                    )
//                }
            }
        }
    }.buildChatRequest(model)

    return groqRequest
}

class CodeError(val value: Int) : Exception()