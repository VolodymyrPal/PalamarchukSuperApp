package com.hfad.palamarchuksuperapp.presentation.common

import com.hfad.palamarchuksuperapp.domain.models.Skill

data class RecyclerSkillFowViewModel(
    val skill: Skill,
    var chosen: Boolean = false,
    var isExpanded: Boolean = false
)
