package com.hfad.palamarchuksuperapp.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result

suspend fun <T> withSqlErrorHandling(
    block: suspend () -> T,
): Result<T, AppError> {
    return try {
        Result.Success(block())
    } catch (e: SQLException) {
        Result.Error(mapSQLException(e))
    }
}