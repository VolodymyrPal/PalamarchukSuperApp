package com.hfad.palamarchuksuperapp.core.data

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

suspend fun <T> withSqlErrorHandling(
    block: suspend () -> T,
): AppResult<T, AppError> {
    return try {
        AppResult.Success(block())
    } catch (e: SQLException) {
        AppResult.Error(mapSQLException(e))
    }
}