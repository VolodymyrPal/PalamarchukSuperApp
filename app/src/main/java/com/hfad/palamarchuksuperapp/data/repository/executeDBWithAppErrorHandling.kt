package com.hfad.palamarchuksuperapp.data.repository

import android.database.SQLException
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result
import com.hfad.palamarchuksuperapp.domain.models.mapSQLException

suspend fun <T> executeDBWithAppErrorHandling(
    block: suspend () -> T,
): Result<T, AppError> {
    return try {
        Result.Success(block())
    } catch (e: SQLException) {
        Result.Error(mapSQLException(e))
    }
}