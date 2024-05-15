package com.hfad.palamarchuksuperapp.data

import java.util.Date
import java.util.UUID

data class Skill(
    val id: UUID? = null,
    val name: String = "",
    val description: String = "",
    val date: Date = Date(),
    val chosen: Boolean = false,
    var isExpanded: Boolean = false,
)

data class SkillDescription (
    val name: String,
    val description: String
)

interface SkillsDataSource {
    fun getSkill(): List<Skill>
    fun deleteSkill(position: Int)
    fun moveToFirstPosition(skill: Skill)
    fun addSkill(skill: Skill)
}