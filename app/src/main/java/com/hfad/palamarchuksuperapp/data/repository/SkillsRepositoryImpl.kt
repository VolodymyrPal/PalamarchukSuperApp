package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.dtos.toSkill
import com.hfad.palamarchuksuperapp.data.dtos.toSkillEntity
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.models.Skill
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    private val skillsDao: SkillsDao,
) : SkillRepository {

    override fun getSkillsFromDB(): Flow<PersistentList<Skill>> =
        skillsDao.getAllSkillsFromDB().map { list -> list.map { it.toSkill() }.toPersistentList() }


    override suspend fun deleteSkill(skill: Skill) =
        skillsDao.deleteSkill(skillEntity = skill.toSkillEntity())


    override suspend fun addSkill(skill: Skill) = skillsDao.addSkill(skill.toSkillEntity())

    override suspend fun updateSkill(skill: Skill) = skillsDao.updateSkill(skill.toSkillEntity())
}

class SkillsRepositoryImplForPreview : SkillRepository {
    override fun getSkillsFromDB(): Flow<PersistentList<Skill>> {
        val one = Skill(
            uuid = UUID.randomUUID(),
            name = "Charles Jarvis",
            description = "neque",
            date = Date(),
            position = 0,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val two = Skill(
            uuid = UUID.randomUUID(),
            name = "David Bowie",
            description = "Some vere interesting description",
            date = Date(),
            position = 1,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val three = Skill(
            uuid = UUID.randomUUID(),
            name = "Freddie Mercury",
            description = "The show must go on",
            date = Date(),
            position = 2,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val four = Skill(
            uuid = UUID.randomUUID(),
            name = "John Deacon",
            description = "Another One Bites the Dust",
            date = Date(),
            position = 3,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val five = Skill(
            uuid = UUID.randomUUID(),
            name = "Brian May",
            description = "We will rock you",
            date = Date(),
            position = 4,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val six = Skill(
            uuid = UUID.randomUUID(),
            name = "Roger Taylor",
            description = "I'm in love with my car",
            date = Date(),
            position = 5,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val seven =
            Skill(
                uuid = UUID.randomUUID(),
                name = "Charles Jarvis",
                description = "neque",
                date = Date(),
                position = 6,
                chosen = false,
                isExpanded = false,
                isVisible = true
            )
        val eight = Skill(
            uuid = UUID.randomUUID(),
            name = "David Bowie",
            description = "Some vere interesting description",
            date = Date(),
            position = 7,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val nine =
            Skill(
                uuid = UUID.randomUUID(),
                name = "Freddie Mercury",
                description = "The show must go on",
                date = Date(),
                position = 8,
                chosen = false,
                isExpanded = false,
                isVisible = true
            )
        val ten = Skill(
            uuid = UUID.randomUUID(),
            name = "John Deacon",
            description = "Another One Bites the Dust",
            date = Date(),
            position = 9,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val eleven =
            Skill(
                uuid = UUID.randomUUID(),
                name = "Brian May",
                description = "We will rock you",
                date = Date(),
                position = 10,
                chosen = false,
                isExpanded = false,
                isVisible = true
            )
        val twelve = Skill(
            uuid = UUID.randomUUID(),
            name = "Roger Taylor",
            description = "I'm in love with my car",
            date = Date(),
            position = 11,
            chosen = false,
            isExpanded = false,
            isVisible = true
        )
        val thirteen = Skill(
            uuid = UUID.randomUUID(),
            name = "Roger Taylor",
            description = "I'm in love with my car",
            date = Date(),
            position = 12,
            chosen = false,
            isExpanded = false,
            isVisible = true
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

    override suspend fun deleteSkill(skill: Skill) {
    }

    override suspend fun addSkill(skill: Skill) {

    }

    override suspend fun updateSkill(skill: Skill) {

    }
}