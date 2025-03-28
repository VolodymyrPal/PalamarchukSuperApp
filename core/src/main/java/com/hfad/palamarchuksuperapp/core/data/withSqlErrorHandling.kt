package com.hfad.palamarchuksuperapp.core.data

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result

suspend fun <T> withSqlErrorHandling(
    block: suspend () -> T,
): Result<T, AppError> {
    return try {
        Result.Success(block())
    } catch (e: SQLException) {
        Result.Error(mapSQLException(e))
    }
}