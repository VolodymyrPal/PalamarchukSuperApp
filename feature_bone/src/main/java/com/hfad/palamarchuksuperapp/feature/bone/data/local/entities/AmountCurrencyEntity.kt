package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Entity
import androidx.room.TypeConverter
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AmountCurrencyEntity(
    val currency: Currency,
    val amount: Float,
)

class AmountCurrencyListConverter {
    @TypeConverter
    fun fromList(list: List<AmountCurrencyEntity>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toList(value: String): List<AmountCurrencyEntity> {
        val list = Json.decodeFromString<List<AmountCurrencyEntity>>(value)
        return list
    }
}