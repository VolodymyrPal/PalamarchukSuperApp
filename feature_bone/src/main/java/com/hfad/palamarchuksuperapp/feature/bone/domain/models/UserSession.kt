package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import com.hfad.palamarchuksuperapp.feature.bone.data.repository.DateAsLongSerializer
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class UserSession(
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val rememberSession: Boolean,
    val userStatus: LogStatus,
    @Serializable(with = DateAsLongSerializer::class) val loginTimestamp: Date,
)