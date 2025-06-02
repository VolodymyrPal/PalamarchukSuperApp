package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.core.data.safeApiCall
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result
import com.hfad.palamarchuksuperapp.data.dtos.toGroqModel
import com.hfad.palamarchuksuperapp.domain.models.AiHandlerInfo
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.repository.AiModelHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GroqApiHandler @AssistedInject constructor(
    private val httpClient: HttpClient,
    @Assisted val initAiHandlerInfo: AiHandlerInfo,
) : AiModelHandler {

    private val _aiHandlerInfo: MutableStateFlow<AiHandlerInfo> =
        MutableStateFlow(initAiHandlerInfo)
    override val aiHandlerInfo: StateFlow<AiHandlerInfo> = _aiHandlerInfo.asStateFlow()
    private val url = "https://api.groq.com/openai/v1/chat/completions"

    override suspend fun getResponse(
        messageList: PersistentList<MessageGroup>,
    ): Result<MessageAI, AppError> {
        return safeApiCall {
            val contextMessages = if (messageList.last().type == MessageType.IMAGE) {
                messageList.last().toGroqRequest(initAiHandlerInfo.model)
            } else {
                messageList.toGroqRequest(initAiHandlerInfo.model)
            }

            val request = httpClient.post(url) {
                header("Authorization", "Bearer ${aiHandlerInfo.value.aiApiKey}")
                contentType(ContentType.Application.Json)
                setBody(
                    contextMessages
                )
            }

            when (request.status.value) {
                in 200..299 -> {
                    val response = request.body<GroqChatCompletionResponse>()
                    val responseText = response.groqChoices[0].groqMessage
                    val responseMessage = MessageAI(
                        message = if (responseText is GroqMessageText) responseText.content else "",
                        model = initAiHandlerInfo.model,
                        messageGroupId = 0 // Handler don't need to know message group
                    )
                    Result.Success(responseMessage)
                }

                in 400..599 -> {
                    val groqError = request.body<GroqError>()
                    Result.Error(
                        error = AppError.CustomError(groqError.error.message),
                        MessageAI(
                            model = initAiHandlerInfo.model,
                            messageGroupId = 0 // Handler don't need to know message group
                        )
                    )
                }

                else -> {
                    Result.Error(
                        error = AppError.NetworkException.ApiError.BadApi(),
                        MessageAI(
                            model = initAiHandlerInfo.model,
                            messageGroupId = 0 // Handler don't need to know message group
                        )
                    )
                }
            }
        }
    }

    override suspend fun getModels(): Result<List<AiModel.GroqModel>, AppError> {
        return safeApiCall {
            val response = httpClient.get(
                "https://api.groq.com/openai/v1/models"
            ) {
                header("Authorization", "Bearer ${aiHandlerInfo.value.aiApiKey}")
                contentType(ContentType.Application.Json)
            }
            when (response.status.value) {
                in 200..299 -> {
                    val list = response.body<GroqModelList>()
                    Result.Success(list.data.map { it.toGroqModel() })
                }

                in 400..599 -> {
                    val groqError = response.body<GroqError>()
                    Result.Error(
                        AppError.NetworkException.ApiError.CustomApiError(
                            groqError.error.message
                        )
                    )
                }

                else -> {
                    Result.Error(
                        error = AppError.NetworkException.ApiError.UndefinedError(
                            message = "Unknown error, please connect developer."
                        )
                    )
                }
            }
        }
    }

    override fun setAiHandlerInfo(aiHandlerInfo: AiHandlerInfo) {
        _aiHandlerInfo.update {
            aiHandlerInfo
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

fun MessageGroup.toGroqRequest(model: AiModel): GroqRequest {
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

fun List<MessageGroup>.toGroqRequest(model: AiModel): GroqRequest {
    val groqRequest = GroqContentBuilder.Builder().also { builder ->
        for (message in this) {
            when (message.type) {
                MessageType.TEXT -> {
                    builder.addMessage(
                        request = message.content.firstOrNull { it.isChosen }?.message
                            ?: message.content.first().message,
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

@AssistedFactory
interface GroqAIApiHandlerFactory {
    fun create(aiHandlerInfo: AiHandlerInfo): GroqApiHandler
}