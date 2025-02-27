package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.dtos.toSkill
import com.hfad.palamarchuksuperapp.data.dtos.toSkillEntity
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(
    private val skillsDao: SkillsDao,
) : SkillRepository {

    private val updatedSkillList = MutableStateFlow(mutableMapOf<UUID, Skill>())

    override fun getSkillsFromDB(): Flow<PersistentList<Skill>> =
        skillsDao.getAllSkillsFromDB().combine(
            updatedSkillList
        ) { dbSkill, updatedSkill ->
            dbSkill.map { skillEntity ->
                updatedSkill[skillEntity.uuid]?.let {
                    Skill(
                        skillEntity = skillEntity,
                        chosen = it.chosen,
                        isExpanded = it.isExpanded,
                        isVisible = it.isVisible
                    )
                } ?: skillEntity.toSkill()
            }.toPersistentList()
        }

//    override fun getSkillsFromDB(): Flow<PersistentList<Skill>> =
//        skillsDao.getAllSkillsFromDB().map { list ->
//            list.map {
//                if (updatedSkillLIst.keys.contains(it.uuid)) {
//                    it.toSkill()
//                }
//                it.toSkill()
//            }.toPersistentList()
//        }

    override suspend fun deleteSkill(skill: Skill) {
        skillsDao.deleteSkill(skillEntity = skill.toSkillEntity())
    }

    override suspend fun addSkill(skill: Skill) {
        skillsDao.insertSkillAutoPosition(skill.toSkillEntity())
    }

    override suspend fun updateSkill(skill: Skill) {
        updatedSkillList.update { currentMap ->
            currentMap.toMutableMap().apply { put(skill.skillEntity.uuid, skill) }
        }
        skillsDao.updateSkill(skill.toSkillEntity())
    }

    override suspend fun moveToFirstPosition(skill: Skill) {
        skillsDao.moveSkill(skill.skillEntity, 0)
    }
}