package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.Skill

interface SkillRepository {
    fun getSkill(): List<Skill>
    fun deleteSkill(position: Int)
    fun addSkill(skill: Skill)
}