package com.hfad.palamarchuksuperapp.domain.models

import com.hfad.palamarchuksuperapp.data.dtos.SkillEntity

data class Skill(
    val skillEntity: SkillEntity,
    var chosen: Boolean = false,
    var isExpanded: Boolean = false,
    var isVisible: Boolean = true,
)