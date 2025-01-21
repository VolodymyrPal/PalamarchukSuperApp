package com.hfad.palamarchuksuperapp.domain.models

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

sealed interface AppError : Error {
    interface Network : AppError {
        enum class RequestError : Network {
            BadRequest, //400
            Unauthorized, //401
            Forbidden, //403 лимиты, ограничения по IP
            NotFound, //404
        }

        enum class ServerError : Network {
            InternalServerError, //500
            BadGateway, //502
            ServiceUnavailable, //503
            GatewayTimeout, //504
        }

    }

    enum class LocalData : AppError {
        DatabaseError, // ошибка базы данных
    }

    enum class OtherErrors : AppError {
        NotImplemented, // не реализовано
        Unknown, // неизвестная ошибка
    }

    data class CustomError(val errorText: String? = null, val error: Throwable? = null) : AppError

    sealed class DatabaseError(
        val message: String? = null, val cause: Throwable? = null,
    ) : Error {

        class UnknownException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /** Sql Error or other database error */
        class SQLException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /** Constraint violation (e.g., unique constraint, foreign key constraint) */
        class ConstraintViolation(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /** Error IO, problem with disk. */
        class DiskIOException(message: String? = null, cause: Throwable? = null) :
            DatabaseError(message, cause)

        /** Not enough memory for storing data. */
        class OutOfMemoryException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

    }
}

class DatabaseException(val error: AppError.DatabaseError) : Exception(error.message, error.cause)


/** Safe wrapper for database calls with retries.

This function executes a database operation wrapped in a suspend block and handles potential exceptions with retries.

 * [retries] (Int):** [Optional] The maximum number of retries to attempt the database operation in case of `android.database.SQLException`. Defaults to 3.
 *
 * [block] (suspend () -> T): The suspend block containing the database operation to be executed. This block should return the result of the operation (type `T`).

 * **Returns:** A `Result<T, AppError.DatabaseError>` representing the outcome of the database operation.
 * With result error possible error: [mapSQLException]
 */
suspend fun <T> safeDbCallWithRetries(
    retries: Int = 3,
    block: suspend () -> T,
): Result<T, AppError.DatabaseError> {

    var retryCount = 0

    return withContext(Dispatchers.IO) {
        try {
            Result.Success<T, AppError.DatabaseError>(block())
        } catch (e: android.database.SQLException) {
            while (retryCount < retries) {
                Log.w(
                    "Database",
                    "Attempt $retryCount to perform database operation failed. Retrying..."
                )
                delay(500)
                retryCount++
            }
            Result.Error(mapSQLException(e))
        } catch (e: Exception) {
            Result.Error(
                AppError.DatabaseError.UnknownException(
                    "Unexpected exception: ${e.message}",
                    e
                )
            )
        }
    }
}

/** Maps an [android.database.SQLException] to a specific [AppError.DatabaseError] subtype.
This function attempts to map a generic `android.database.SQLException` to a more specific subtype of
`AppError.DatabaseError` based on the exception class.

 * e : [android.database.SQLException]: The SQLException to be mapped.
 *
 * Possible [AppError.DatabaseError] outcome: [AppError.DatabaseError.ConstraintViolation],
 * [AppError.DatabaseError.DiskIOException],
 * [AppError.DatabaseError.OutOfMemoryException], [AppError.DatabaseError.SQLException]
 */
private fun mapSQLException(e: android.database.SQLException): AppError.DatabaseError {
    return when (e) {
        is SQLiteConstraintException -> AppError.DatabaseError.ConstraintViolation(e.message, e)
        is SQLiteDiskIOException -> AppError.DatabaseError.DiskIOException(e.message, e)
        is SQLiteFullException -> AppError.DatabaseError.OutOfMemoryException(e.message, e)
        else -> AppError.DatabaseError.SQLException(e.message, e)
    }
}