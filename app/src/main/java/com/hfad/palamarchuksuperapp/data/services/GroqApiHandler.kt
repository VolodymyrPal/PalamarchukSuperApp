package com.hfad.palamarchuksuperapp.data.services

import android.util.Log
import coil.network.HttpException
import com.hfad.palamarchuksuperapp.BuildConfig
import com.hfad.palamarchuksuperapp.data.entities.MessageAI
import com.hfad.palamarchuksuperapp.data.repository.AiModels
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
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GroqApiHandler @Inject constructor(
    private val httpClient: HttpClient,
) : AiModelHandler {
    private val apiKey = BuildConfig.GROQ_KEY
    private val max_tokens = 1024
    private val adminRoleMessage: Message = GroqContentBuilder.Builder().let {
        it.role = "system"
        it.buildText(
            "You are math tutor. User send you image with sample. " +
                    "Please provide answer and solve this sample."
        )
    }
    val errorFlow = MutableStateFlow<DataError?>(null)

    private val url = "https://api.groq.com/openai/v1/chat/completions"

    override suspend fun getResponse(
        messageList: PersistentList<MessageAI>,
        model: AiModels?,
    ): Result<MessageAI, AppError> {

        val request = httpClient.post(url) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                ""
              // messageList.toGroqRequest(model ?: AiModels.GroqModels.GROQ_IMAGE) // TODO logic for different models
            )
        }
        try {

            Log.d("Groq response:", request.body<String>())

            if (request.status == HttpStatusCode.OK) {

                val response = request.body<ChatCompletionResponse>()

                val responseMessage = GroqContentBuilder.Builder().let {
                    it.role = "assistant"
                    it.buildText((response.choices[0].message as MessageText).content)
                }

            } else {
                throw CodeError(request.status.value)
            }
        } catch (e: Exception) {
            errorFlow.emit(
                when (e) {
                    is HttpException -> {
                        AppError.Network.InternalServerError
                    }

                    is CodeError -> {
                        when (e.value) {
                            400 -> {
                                //DataError.Network.BadRequest
                                AppError.CustomError(e.value.toString())
                            }

                            401 -> {
                                AppError.CustomError(e.value.toString())
                                //DataError.Network.Unauthorized
                            }

                            403 -> {
                                //DataError.Network.Forbidden
                                AppError.CustomError(e.value.toString())

                            }

                            in 400..500 -> {
                                AppError.CustomError(e.value.toString())
                                //DataError.Network.InternalServerError
                            }

                            in 500..600 -> {
                                AppError.CustomError(e.value.toString())
                                //DataError.Network.Unknown
                            }

                            else -> {
                                AppError.CustomError(e.value.toString())
                                //DataError.Network.Unknown
                            }
                        }
                    }

                    else -> {
                        AppError.CustomError(e.toString())
                    }
                }
            )
        }

        return Result.Success(MessageAI("", ""))
    }
}


class GroqContentBuilder {

    class Builder {

        private var contents: MutableList<GroqContentType> = arrayListOf()

        var role = "user"

        @JvmName("addPart")
        fun <T : GroqContentType> content(data: T) = apply { contents.add(data) }

        @JvmName("addText")
        fun text(text: String) = content(ContentText(text = text))

        @JvmName("addImage")
        fun image(image: Base64) = content(ContentImage(image_url = ImageUrl(image)))

        //fun build(): MessageText = MessageText(content = contents, role = role)
        fun buildChat(): MessageChat = MessageChat(content = contents, role = role)

        fun buildText(request: String): MessageText = MessageText(content = request, role = role)
    }
}

class CodeError(val value: Int) : Exception()