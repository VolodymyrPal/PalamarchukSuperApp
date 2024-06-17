package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "mySkillsDB")
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo (name = "uuid") val uuid: UUID? = null,
    @ColumnInfo (name = "name") val name: String = "",
    @ColumnInfo (name = "description") val description: String = "",
    @ColumnInfo (name = "studiedDate") val date: Date = Date(),
)