package com.hfad.palamarchuksuperapp.data.entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date
import java.util.UUID

@Entity(tableName = "mySkillsDB")
@TypeConverters (Converters::class)
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo (name = "uuid") val uuid: UUID? = null,
    @ColumnInfo (name = "name") val name: String = "",
    @ColumnInfo (name = "description") val description: String = "",
    @ColumnInfo (name = "studiedDate") val date: Date = Date(),
)

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromDateTime(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToDateTime(date: Date?): Long? {
        return date?.time
    }
}