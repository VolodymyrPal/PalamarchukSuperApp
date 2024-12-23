package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.database.DATABASE_SKILLS_NAME
import com.hfad.palamarchuksuperapp.data.dtos.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillsDao {
    @Query("SELECT * FROM $DATABASE_SKILLS_NAME ORDER BY Position ASC")
    fun getAllSkillsFromDB(): Flow<List<SkillEntity>>

    @Insert
    suspend fun addSkill(skillEntity: SkillEntity)

    @Update
    suspend fun updateSkill(skillEntity: SkillEntity)

    @Delete
    suspend fun deleteSkill(skillEntity: SkillEntity)
}