package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hfad.palamarchuksuperapp.data.entities.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillsDao {
    @Query("SELECT * FROM mySkillsDB")
    fun getAll(): Flow<List<Skill>>

    @Insert
    suspend fun insertFriend(skill: Skill)

    @Delete
    suspend fun deleteAllMyFriends(allMySkills: List<Skill>)
}