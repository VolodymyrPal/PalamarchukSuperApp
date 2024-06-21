package com.hfad.palamarchuksuperapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.data.database.DATABASE_NAME
import java.util.Date
import java.util.UUID

@Entity(tableName = DATABASE_NAME)
@TypeConverters (Converters::class)
data class Skill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo (name = "id") val id: Int = 0,
    @ColumnInfo (name = "uuid") val uuid: UUID = UUID.randomUUID(),
    @ColumnInfo (name = "name") val name: String = "",
    @ColumnInfo (name = "description") val description: String = "",
    @ColumnInfo (name = "studiedDate") val date: Date = Date(),
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