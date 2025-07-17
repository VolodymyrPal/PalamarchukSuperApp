package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GptRequested(
    val model: String = "gpt-4o-mini",
    val messages: List<RequestRole>,
    val temperature: Double = 0.7,
)

@Serializable
data class RequestRole(
    val role: String,
    val content: List<MessageRequest>,
)

@Serializable
sealed class MessageRequest

@SerialName("text")
@Serializable
data class TextMessageRequest (
    val text: String? = null
) : MessageRequest()

@SerialName("image_url")
@Serializable
data class ImageMessageRequest (
    @SerialName("image_url")
    val imageUrl: ImageRequest?,
) : MessageRequest()

@Serializable
data class ImageRequest (
    val url: String
)



@Serializable
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    @SerialName("system_fingerprint") val systemFingerprint: String?
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    @SerialName("logprobs") val logProbs: Int? = null,
    @SerialName("finish_reason") val finishReason: String
)

@Serializable
data class Message(
    val role: String,
    val content: String,
    val refusal: String? = null
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int,
    @SerialName("prompt_tokens_details") val promptTokensDetails: TokensDetails,
    @SerialName("completion_tokens_details") val completionTokensDetails: TokensDetails
)

@Serializable
data class TokensDetails(
    @SerialName("cached_tokens") val cachedTokens: Int? = null ,
    @SerialName("audio_tokens") val audioTokens: Int? = null,
    @SerialName("reasoning_tokens") val reasoningTokens: Int? = null,
    @SerialName("accepted_prediction_tokens") val acceptedPredictionTokens: Int? = null,
    @SerialName("rejected_prediction_tokens") val rejectedPredictionTokens: Int? = null
)

@Serializable
data class OpenAIError(
    val error: OpenAIErrorDetails
)

@Serializable
data class OpenAIErrorDetails(
    val message: String,
    val type: String,
    val param: String?, // Can be null
    val code: String
)