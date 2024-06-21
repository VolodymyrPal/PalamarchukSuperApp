package com.hfad.palamarchuksuperapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.dao.SkillsDao
import com.hfad.palamarchuksuperapp.data.entities.Converters
import com.hfad.palamarchuksuperapp.data.entities.Skill

@Database (entities = [Skill::class], version = 1)
abstract class SkillsDatabase : RoomDatabase() {
    open abstract fun skillsDao(): SkillsDao
    companion object {
        @Volatile
        private var INSTANCE: SkillsDatabase? = null

        fun getMySkillsDatabase(context: Context): SkillsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = SkillsDatabase::class.java,
                    name = "mySkillsDB"
                ).build().also { INSTANCE = it }
            }
        }
    }
}