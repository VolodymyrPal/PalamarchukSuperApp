package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "mySkillsDB")
data class Skill(
    @PrimaryKey(autoGenerate = true) val numbering: Int = 0,
    @ColumnInfo (name = "uniqID") val id: UUID? = null,
    @ColumnInfo (name = "skillName") val name: String = "",
    @ColumnInfo (name = "skillDescription") val description: String = "",
    @ColumnInfo (name = "skillDateStudied") val date: Date = Date(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo (name = "uuid") val uuid: UUID? = null,
)