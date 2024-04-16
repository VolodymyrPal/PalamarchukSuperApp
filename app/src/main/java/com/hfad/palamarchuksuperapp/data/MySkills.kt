package com.hfad.palamarchuksuperapp.data

import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class Skill (
    val id: UUID? = null,
    val name: String = "",
    val description: String = "",
    val date: Date = Date(),
    val chosen: Boolean = false,
    )