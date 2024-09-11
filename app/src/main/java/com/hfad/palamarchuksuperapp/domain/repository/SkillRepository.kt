package com.hfad.palamarchuksuperapp.domain.repository

import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getSkillsFromDB(): Flow<List<SkillDomainRW>>
    suspend fun deleteSkill(skill: SkillDomainRW)
    suspend fun addSkill(skill: SkillDomainRW)
    suspend fun updateSkill(skill: SkillDomainRW)
}