package com.hfad.palamarchuksuperapp.domain.models

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import com.hfad.palamarchuksuperapp.domain.models.AppError.DatabaseError

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

    enum class OtherErrors : AppError {
        NotImplemented, // не реализовано
        Unknown, // неизвестная ошибка
    }

    data class CustomError(val errorText: String? = null, val error: Throwable? = null) : AppError

    /**
     * Represents a sealed hierarchy of database-related errors.
     * Each subclass corresponds to a specific type of database error.
     *
     * @property message A detailed error message (optional).
     * @property cause The root cause of the error, if available (optional).
     */
    sealed class DatabaseError(
        val message: String? = null, val cause: Throwable,
    ) : AppError {

        /**
         * Represents a general SQL error or other database-related issue.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class UnhandledSQLException(
            message: String? = null,
            cause: Throwable,
        ) : DatabaseError(message, cause)

        /**
         * Represents a constraint violation (e.g., unique constraint, foreign key constraint).
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class ConstraintViolation(
            message: String? = null,
            cause: Throwable,
        ) : DatabaseError(message, cause)

        /**
         * Represents a disk I/O error, such as a problem with reading/writing to disk.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class DiskIOException(message: String? = null, cause: Throwable) :
            DatabaseError(message, cause)

        /**
         * Represents an error indicating insufficient memory for database operations.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class OutOfMemoryException(
            message: String? = null,
            cause: Throwable,
        ) : DatabaseError(message, cause)

    }
}

/** Maps an [SQLException] to a specific [AppError.DatabaseError] subtype.
 *
 * @param e the SQLException to be mapped.
 *
 * @return A specific subtype of [AppError.DatabaseError] corresponding to the given SQLException.
 * Possible outcomes include:
 * - [DatabaseError.ConstraintViolation]
 * - [DatabaseError.DiskIOException]
 * - [DatabaseError.OutOfMemoryException]
 * - [DatabaseError.UnhandledSQLException]
 */
fun mapSQLException(e: SQLException): DatabaseError {
    return when (e) {
        is SQLiteConstraintException -> DatabaseError.ConstraintViolation(e.message, e)
        is SQLiteDiskIOException -> DatabaseError.DiskIOException(e.message, e)
        is SQLiteFullException -> DatabaseError.OutOfMemoryException(e.message, e)
        else -> DatabaseError.UnhandledSQLException(e.message, e)
    }
}