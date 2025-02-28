package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.database.DATABASE_SKILLS_NAME
import com.hfad.palamarchuksuperapp.data.dtos.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillsDao {
    @Query("SELECT * FROM $DATABASE_SKILLS_NAME ORDER BY Position ASC")
    fun getAllSkillsFromDB(): Flow<List<SkillEntity>>

    @Insert
    suspend fun insertSkill(skillEntity: SkillEntity)

    @Insert
    fun insertSkills(skills: List<SkillEntity>)

    @Update
    suspend fun updateSkill(skillEntity: SkillEntity)

    @Delete
    suspend fun deleteSkill(skillEntity: SkillEntity)

    @Query("SELECT MAX(Position) FROM $DATABASE_SKILLS_NAME")
    suspend fun getMaxPosition(): Int?

    @Transaction
    suspend fun insertSkillAutoPosition(skill: SkillEntity) {
        val newPosition = (getMaxPosition() ?: 0) + 1
        insertSkill(skill.copy(position = newPosition))
    }


    @Query("UPDATE $DATABASE_SKILLS_NAME SET Position = :newPosition WHERE SkillID = :skillId")
    suspend fun updateSkillPosition(skillId: Int, newPosition: Int)

    @Query(
        "UPDATE $DATABASE_SKILLS_NAME " +
                "SET Position = Position + 1 " +
                "WHERE Position >= :newPosition AND Position <= :oldPosition"
    )
    suspend fun shiftPositionsUp(newPosition: Int, oldPosition: Int)

    @Query(
        "UPDATE $DATABASE_SKILLS_NAME SET Position = Position - 1 " +
                "WHERE Position >= :oldPosition AND Position <= :newPosition"
    )
    suspend fun shiftPositionsDown(oldPosition: Int, newPosition: Int)

    @Transaction
    suspend fun moveSkill(skill: SkillEntity, newPosition: Int) {
        val oldPosition = skill.position
        if (oldPosition == newPosition) return
        updateSkillPosition(skill.id, -1)
        if (newPosition < oldPosition) {
            shiftPositionsUp(newPosition, oldPosition)
        } else {
            shiftPositionsDown(oldPosition, newPosition)
        }
        updateSkillPosition(skill.id, newPosition)
    }
}