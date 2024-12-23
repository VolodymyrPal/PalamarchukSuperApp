package com.hfad.palamarchuksuperapp.domain.models

import java.util.Date
import java.util.UUID

data class Skill(
    val id: Int = 0,
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "",
    val description: String = "",
    val date: Date = Date(),
    val position: Int = id,
    var chosen: Boolean = false,
    var isExpanded: Boolean = false,
    var isVisible: Boolean = true
)