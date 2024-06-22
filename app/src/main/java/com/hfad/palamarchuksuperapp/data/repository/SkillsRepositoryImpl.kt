package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    private val skillsDao: SkillsDao
) : SkillRepository {

    override fun getSkillsFromDB(): Flow<List<Skill>> = skillsDao.getAllSkillsFromDB()

    override suspend fun deleteSkill(skill: Skill) = skillsDao.deleteSkill(skill = skill)


    override suspend fun addSkill(skill: Skill) = skillsDao.addSkill(skill)

    override suspend fun updateSkill(skill: Skill) = skillsDao.updateSkill(skill)
}

class SkillsRepositoryImplForPreview : SkillRepository {
    override fun getSkillsFromDB(): Flow<List<Skill>> {
        val one = Skill(name = "some skills", description = "One, Two, Three", uuid = UUID.randomUUID())
        val two = Skill(name = "XML", description = "One, Two, Three", uuid = UUID.randomUUID())
        val three = Skill(name = "Recycler View", description = "Paging, DiffUtil, Adapter", uuid = UUID.randomUUID())
        val four = Skill(name = "Retrofit", description = "One, Two, Three", uuid = UUID.randomUUID())
        val five = Skill(name = "Async", description = "One, Two, Three", uuid = UUID.randomUUID())
        val six = Skill(name = "Dependency injections", description = "Multi-module architecture, Component Dependencies, Multibindings, " + "Components, Scope, Injections" + " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin", uuid = UUID.randomUUID())
        val seven = Skill(name = "RxJava", description = "One, Two, Three", uuid = UUID.randomUUID())
        val eight = Skill(name = "To learn", description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, " + "RESTful API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",uuid = UUID.randomUUID())
        val nine = Skill(name = "Recycler View", description = "One, Two, Three", uuid = UUID.randomUUID())
        val ten = Skill(name = "Project Pattern", description = "MVVM", uuid = UUID.randomUUID())
        val eleven = Skill(name = "Compose", description = "Composition tracing", uuid = UUID.randomUUID())
        val twelve = Skill(name = "Work with Image", description = "FileProvider,Coil", uuid = UUID.randomUUID())
        val thirteen = Skill(name = "Work with Image", description = "FileProvider,Coil", uuid = UUID.randomUUID())
        val _skillsList = MutableStateFlow(listOf( one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen))
        return _skillsList
    }

    override suspend fun deleteSkill(skill: Skill) {
    }

    override suspend fun addSkill(skill: Skill) {

    }

    override suspend fun updateSkill(skill: Skill) {

    }
}