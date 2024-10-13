package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.ui.common.toDomainRW
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    private val skillsDao: SkillsDao,
) : SkillRepository {

    override fun getSkillsFromDB(): Flow<PersistentList<SkillDomainRW>> =
        skillsDao.getAllSkillsFromDB().map { list -> list.map { it.toDomainRW() }.toPersistentList() }


    override suspend fun deleteSkill(skill: SkillDomainRW) =
        skillsDao.deleteSkill(skill = skill.skill)


    override suspend fun addSkill(skill: SkillDomainRW) = skillsDao.addSkill(skill.skill)

    override suspend fun updateSkill(skill: SkillDomainRW) = skillsDao.updateSkill(skill.skill)
}

class SkillsRepositoryImplForPreview : SkillRepository {
    override fun getSkillsFromDB(): Flow<PersistentList<SkillDomainRW>> {
        val one = SkillDomainRW(
            Skill(name = "some skills", description = "One, Two, Three", uuid = UUID.randomUUID())
        )
        val two = SkillDomainRW(
            Skill(
                name = "XML",
                description = "One, Two, Three",
                uuid = UUID.randomUUID()
            )
        )
        val three = SkillDomainRW(
            Skill(
                name = "Recycler View",
                description = "Paging, DiffUtil, Adapter",
                uuid = UUID.randomUUID()
            )
        )
        val four = SkillDomainRW(
            Skill(name = "Retrofit", description = "One, Two, Three", uuid = UUID.randomUUID())
        )
        val five = SkillDomainRW(
            Skill(
                name = "Async",
                description = "One, Two, Three",
                uuid = UUID.randomUUID()
            )
        )
        val six = SkillDomainRW(
            Skill(
                name = "Dependency injections",
                description = "Multi-module architecture, Component Dependencies, Multibindings, " + "Components, Scope, Injections" + " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
                uuid = UUID.randomUUID()
            )
        )
        val seven =
            SkillDomainRW(
                Skill(
                    name = "RxJava",
                    description = "One, Two, Three",
                    uuid = UUID.randomUUID()
                )
            )
        val eight = SkillDomainRW(
            Skill(
                name = "To learn",
                description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, " + "RESTful API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",
                uuid = UUID.randomUUID()
            )
        )
        val nine =
            SkillDomainRW(
                Skill(
                    name = "Recycler View",
                    description = "One, Two, Three",
                    uuid = UUID.randomUUID()
                )
            )
        val ten = SkillDomainRW(
            Skill(
                name = "Project Pattern",
                description = "MVVM",
                uuid = UUID.randomUUID()
            )
        )
        val eleven =
            SkillDomainRW(
                Skill(
                    name = "Compose",
                    description = "Composition tracing",
                    uuid = UUID.randomUUID()
                )
            )
        val twelve = SkillDomainRW(
            Skill(
                name = "Work with Image",
                description = "FileProvider,Coil",
                uuid = UUID.randomUUID()
            )
        )
        val thirteen = SkillDomainRW(
            Skill(
                name = "Work with Image",
                description = "FileProvider,Coil",
                uuid = UUID.randomUUID()
            )
        )
        val _skillsList = MutableStateFlow(
            persistentListOf(
                one,
                two,
                three,
                four,
                five,
                six,
                seven,
                eight,
                nine,
                ten,
                eleven,
                twelve,
                thirteen
            )
        )
        return _skillsList
    }

    override suspend fun deleteSkill(skill: SkillDomainRW) {
    }

    override suspend fun addSkill(skill: SkillDomainRW) {

    }

    override suspend fun updateSkill(skill: SkillDomainRW) {

    }
}