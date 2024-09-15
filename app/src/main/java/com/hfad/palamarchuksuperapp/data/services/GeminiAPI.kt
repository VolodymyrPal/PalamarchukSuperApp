package com.hfad.palamarchuksuperapp.data.services

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent> = listOf(GeminiContent()),
)
@Serializable
data class GeminiContent(
    val parts: List<Part> = listOf(Part()),
)

@Serializable
data class Part(
    val text: String = "Is my request completed?",
)