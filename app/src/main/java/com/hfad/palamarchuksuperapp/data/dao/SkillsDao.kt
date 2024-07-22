package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.database.DATABASE_SKILLS_NAME
import com.hfad.palamarchuksuperapp.data.entities.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillsDao {
    @Query("SELECT * FROM $DATABASE_SKILLS_NAME")
    fun getAllSkillsFromDB(): Flow<List<Skill>>

    @Insert
    suspend fun addSkill(skill: Skill)

    @Update
    suspend fun updateSkill(skill: Skill)

    @Delete
    suspend fun deleteSkill(skill: Skill)
}