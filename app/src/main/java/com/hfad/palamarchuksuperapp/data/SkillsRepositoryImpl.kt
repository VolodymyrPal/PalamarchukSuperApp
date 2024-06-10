package com.hfad.palamarchuksuperapp.data

import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    //private val skillsDataBase: SkillDataBase
) : SkillRepository {

    override fun getSkillsFromDB(): List<Skill> {
        return emptyList()
    }

    override fun deleteSkill(position: Int) {

    }

    override fun addSkill(skill: Skill) {

    }
}