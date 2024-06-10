package com.hfad.palamarchuksuperapp.presentation.common

import com.hfad.palamarchuksuperapp.domain.models.Skill

data class SkillDomainRW(
    val skill: Skill,
    var chosen: Boolean = false,
    var isExpanded: Boolean = false,
    var isVisible: Boolean = true
)

interface Mapper<in From, out To> {
    fun map(from: From): To
}

object SkillToSkillDomain : Mapper<Skill, SkillDomainRW> {
    override fun map(from: Skill) = SkillDomainRW (
        skill = from,
    )
}

object SkillDomainToSkill : Mapper<SkillDomainRW, Skill> {
    override fun map(from: SkillDomainRW): Skill {
        return from.skill
    }
}