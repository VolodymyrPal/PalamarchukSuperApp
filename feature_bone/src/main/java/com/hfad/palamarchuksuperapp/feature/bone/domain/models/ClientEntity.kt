package com.hfad.palamarchuksuperapp.feature.bone.domain.models

import androidx.room.PrimaryKey

/**
 * Base client entity class to use through the app.
 *
 * Each operation has its own business entity.
 */
data class ClientEntity(
    @PrimaryKey
    val code: Int = 1,
    val name: String = " Base card ",
    val type: EntityType = EntityType.OTHER,
    val manager: String = "",
)

enum class EntityType {
    HOLDING, RESIDENT, NONRESIDENT, FACTORY, OTHER
}

data class EntityDetails(
    val name: String = "",
)