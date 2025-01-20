package com.hfad.palamarchuksuperapp.domain.models

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface AppError : Error {
    interface Network : AppError {
        enum class RequestError: Network {
            BadRequest, //400
            Unauthorized, //401
            Forbidden, //403 лимиты, ограничения по IP
            NotFound, //404
        }
        enum class ServerError: Network {
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

    data class CustomError (val errorText: String? = null, val error: Throwable? = null): AppError

    sealed class DatabaseError(
        val message: String? = null, val cause: Throwable? = null,
    ) : Error {

        /** Custom logic exception. Error in business logic, like not found, validation, etc. */
        class LogicException(message: String, cause: Throwable) : DatabaseError(message, cause)

        /** Sql Error or other database error */
        class SQLException(message: String? = null, cause: Throwable? = null) :
            DatabaseError(message, cause)

        /** Constraint violation (e.g., unique constraint, foreign key constraint) */
        class ConstraintViolation(
            message: String? = null,
            cause: Throwable? = null,
        ) :
            DatabaseError(message, cause)

        /** Error IO, problem with disk. */
        class DiskIOException(message: String? = null, cause: Throwable? = null) :
            DatabaseError(message, cause)

        /** Not enough memory for storing data. */
        class OutOfMemoryException(message: String? = null, cause: Throwable? = null) :
            DatabaseError(message, cause)

    }
}

class DatabaseException(val error: AppError.DatabaseError) : Exception(error.message, error.cause)

suspend fun <T> safeDbCall(block: suspend () -> T): Result<T, AppError.DatabaseError> =
    withContext(Dispatchers.IO) {
        try {
            Result.Success(block())
        } catch (e: android.database.SQLException) {
            val databaseError = mapSQLException(e)
            Result.Error(databaseError)
        } catch (e: Exception) {
            Result.Error(
                AppError.DatabaseError.LogicException(
                    "Unexpected exception occurred: ${e.message}",
                    e
                )
            )
        }
    }

private fun mapSQLException(e: android.database.SQLException): AppError.DatabaseError {
    return when (e) {
        is SQLiteConstraintException -> AppError.DatabaseError.ConstraintViolation(e.message, e)
        is SQLiteDiskIOException -> AppError.DatabaseError.DiskIOException(e.message, e)
        is SQLiteFullException -> AppError.DatabaseError.OutOfMemoryException(e.message, e)
        else -> AppError.DatabaseError.SQLException(e.message, e)
    }
}