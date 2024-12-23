package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.domain.models.Skill
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getSkillsFromDB(): Flow<PersistentList<Skill>>
    suspend fun deleteSkill(skill: Skill)
    suspend fun addSkill(skill: Skill)
    suspend fun updateSkill(skill: Skill)
}