package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.data.entities.Skill
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    suspend fun getSkillsFromDB(): List<Skill>
    suspend fun deleteSkill(skill: Skill)
    suspend fun addSkill(skill: Skill)
}