package com.hfad.palamarchuksuperapp.core.data

import android.database.SQLException
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

suspend fun <T : Any?> fetchWithCacheFallback(
    fetchRemote: suspend () -> T,
    storeAndRead: suspend (T) -> T,
    fallbackFetch: suspend () -> T,
): AppResult<T, AppError> {

    val remoteData = try {
        fetchRemote()
    } catch (apiEx: Exception) {
        return try {
            val localData = fallbackFetch()
            AppResult.Error(data = localData, error = mapApiException(apiEx))
        } catch (dbEx: SQLException) {
            AppResult.Error(AppError.CustomError("Problem with internet and database: ${apiEx.message}, ${dbEx.message}"))
        }
    }

    return try {
        val localData = storeAndRead(remoteData)
        AppResult.Success(localData)
    } catch (dbEx: SQLException) {
        AppResult.Success(data = remoteData, error = mapSQLException(dbEx))
    }
}