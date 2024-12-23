package com.hfad.palamarchuksuperapp.data.dtos

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_SKILLS_NAME
import java.util.Date
import java.util.UUID

@Entity(tableName = DATABASE_SKILLS_NAME)
@TypeConverters (Converters::class)
@Stable
data class SkillEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo (name = "SkillID") val id: Int = 0,
    @ColumnInfo (name = "SkillUUID") val uuid: UUID = UUID.randomUUID(),
    @ColumnInfo (name = "SkillName") val name: String = "",
    @ColumnInfo (name = "Description") val description: String = "",
    @ColumnInfo (name = "StudiedDate") val date: Date = Date(),
    @ColumnInfo (name = "Position") val position: Int = id
)

class Converters {
    @TypeConverter
    fun fromDateTime(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToDateTime(date: Date?): Long? {
        return date?.time
    }
}