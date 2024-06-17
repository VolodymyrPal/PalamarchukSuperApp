package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import java.util.UUID
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    //private val skillsDataBase: SkillDataBase
) : SkillRepository {

    override fun getSkillsFromDB(): List<Skill> {
        val one =
            Skill(
                name = "some skills",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )

        val two = Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID())
        val three = Skill(
            name = "Recycler View",
            description = "Paging, DiffUtil, Adapter",
            id = UUID.randomUUID()
        )

        val four =
            Skill(
                name = "Retrofit",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )

        val five =
            Skill(
                name = "Async",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )

        val six = Skill(
            name = "Dependency injections",
            description = "Multi-module architecture, Component Dependencies, Multibindings, " +
                    "Components, Scope, Injections" +
                    " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
            id = UUID.randomUUID()
        )

        val seven =
            Skill(
                name = "RxJava",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )

        val eight = Skill(
            name = "To learn",
            description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, " +
                    "RESTful API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",
            id = UUID.randomUUID()
        )

        val nine = Skill(
            name = "Recycler View",
            description = "One, Two, Three",
            id = UUID.randomUUID()
        )

        val ten = Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID())
        val eleven = Skill(
            name = "Compose",
            description = "Composition tracing",
            id = UUID.randomUUID()
        )

        val twelve = Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )


        val thirteen = Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )

        val _skillsList = listOf(
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
        return _skillsList
    }

    override fun deleteSkill(position: Int) {

    }

    override fun addSkill(skill: Skill) {

    }
}