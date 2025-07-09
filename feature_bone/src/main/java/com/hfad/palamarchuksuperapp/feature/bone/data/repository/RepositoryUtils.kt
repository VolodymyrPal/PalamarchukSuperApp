package com.hfad.palamarchuksuperapp.feature.bone.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.data.mapSQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

fun <T> trySqlApp(
    block: () -> T,
): AppResult<T, AppError> {
    return try {
        AppResult.Success(block())
    } catch (e: SQLException) {
        AppResult.Error(mapSQLException(e))
    }
} 