package com.hfad.palamarchuksuperapp.domain.models

import java.util.Date
import java.util.UUID

data class Skill(
    val id: UUID? = null,
    val name: String = "",
    val description: String = "",
    val date: Date = Date(),
)