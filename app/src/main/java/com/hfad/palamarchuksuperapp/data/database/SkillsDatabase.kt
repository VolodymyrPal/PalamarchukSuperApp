package com.hfad.palamarchuksuperapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.dtos.Converters
import com.hfad.palamarchuksuperapp.data.dtos.SkillEntity

@Database(entities = [SkillEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class SkillsDatabase : RoomDatabase() {
    abstract fun skillsDao(): SkillsDao
}
const val DATABASE_SKILLS_NAME = "mydatabase"
const val DATABASE_PROJECT_NAME = "myDatabaseRoom"