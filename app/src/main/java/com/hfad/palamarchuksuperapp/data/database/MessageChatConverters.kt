package com.hfad.palamarchuksuperapp.data.database

import androidx.room.TypeConverter
import com.hfad.palamarchuksuperapp.domain.models.AiModel
import com.hfad.palamarchuksuperapp.domain.models.MessageAiContent
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.Json

class MessageChatConverters { //TODO
    @TypeConverter
    fun fromRole(role: Role): String = role.value

    @TypeConverter
    fun toRole(value: String): Role = Role.entries.first { it.value == value }

    @TypeConverter
    fun fromMessageType(type: MessageType): String = type.name

    @TypeConverter
    fun toMessageType(value: String): MessageType = MessageType.valueOf(value)

    @TypeConverter
    fun fromAiModel(model: AiModel?): String? = model?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toAiModel(value: String?): AiModel? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromMessageAiContent(content: MessageAiContent?): String? =
        content?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toMessageAiContent(value: String?): MessageAiContent? =
        value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromPersistentList(list: PersistentList<*>): String = Json.encodeToString(list)

    @TypeConverter
    fun toPersistentList(value: String): PersistentList<*> = try {
        Json.decodeFromString(value)
    } catch (e: Exception) {
        persistentListOf<Any>()
    }
} 