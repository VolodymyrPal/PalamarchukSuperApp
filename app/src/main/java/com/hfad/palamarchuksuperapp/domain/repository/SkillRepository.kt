package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.Skill

interface SkillRepository {
    fun getSkillsFromDB(): List<Skill>
    suspend fun deleteSkill(skill: Skill)
    suspend fun addSkill(skill: Skill)
}