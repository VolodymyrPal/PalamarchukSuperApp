package com.hfad.palamarchuksuperapp.domain.models

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import com.hfad.palamarchuksuperapp.domain.models.AppError.DatabaseError

sealed class AppError(
    override val cause: Throwable? = null,
    override val message: String? = null,
) : Throwable(message, cause), Error {

    sealed class NetworkException : AppError() {
        /**
         * Represents a sealed hierarchy of request-related errors.
         * Each subclass corresponds to a specific type of request error.
         *
         * @property message A detailed error message (optional).
         * @property cause The root cause of the error, if available (optional).
         */
        sealed class RequestError(
            override val message: String? = null,
            override val cause: Throwable? = null,
        ) : NetworkException() {
            class BadRequest(message: String? = null, cause: Throwable? = null) :
                RequestError(message, cause)//400

            class Unauthorized(message: String? = null, cause: Throwable? = null) :
                RequestError(message, cause)//401

            class Forbidden(message: String? = null, cause: Throwable? = null) :
                RequestError(message, cause)//403 лимиты, ограничения по IP

            class NotFound(message: String? = null, cause: Throwable? = null) :
                RequestError(message, cause)//404
        }

        /**
         * Represents a sealed hierarchy of server-related errors.
         * Each subclass corresponds to a specific type of server error.
         *
         * @property message A detailed error message (optional).
         * @property cause The root cause of the error, if available (optional).
         */
        sealed class ServerError(
            override val message: String? = null,
            override val cause: Throwable? = null,
        ) : NetworkException() {
            class InternalServerError(message: String? = null, cause: Throwable? = null) :
                ServerError(message, cause)//500

            class BadGateway(message: String? = null, cause: Throwable? = null) :
                ServerError(message, cause)//502

            class ServiceUnavailable(message: String? = null, cause: Throwable? = null) :
                ServerError(message, cause)//503

            class GatewayTimeout(message: String? = null, cause: Throwable? = null) :
                ServerError(message, cause)//504
        }

    }

    data class CustomError(val errorText: String? = null, val error: Throwable? = null) : AppError(
        cause = error,
        message = errorText
    )

    /**
     * Represents a sealed hierarchy of database-related errors.
     * Each subclass corresponds to a specific type of database error.
     *
     * @property message A detailed error message (optional).
     * @property cause The root cause of the error, if available (optional).
     */

    sealed class DatabaseError(
        override val message: String? = null, override val cause: Throwable? = null,
    ) : AppError(cause, message) {

        /**
         * Represents a general SQL error or other database-related issue.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class UnhandledSQLException(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /**
         * Represents a constraint violation (e.g., unique constraint, foreign key constraint).
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class ConstraintViolation(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /**
         * Represents a disk I/O error, such as a problem with reading/writing to disk.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class DiskIOException(message: String? = null, cause: Throwable? = null) :
            DatabaseError(message, cause)

        /**
         * Represents an error indicating insufficient memory for database operations.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class OutOfMemoryException(
            message: String? = null,
            cause: Throwable? = null,
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

/** Maps an [Exception] to a specific [AppError.NetworkException] subtype.
 *
 * @param e the Exception to be mapped.
 *
 * @return A specific subtype of [AppError.NetworkException] corresponding to the given Exception.
 * Possible outcomes include:
 * - [AppError.NetworkException.RequestError.BadRequest]
 * - [AppError.NetworkException.RequestError.Unauthorized]
 * - [AppError.NetworkException.RequestError.Forbidden]
 * - [AppError.NetworkException.ServerError.InternalServerError]
 * - [AppError.NetworkException.ServerError.BadGateway]
 * - [AppError.NetworkException.ServerError.ServiceUnavailable]
 * - [AppError.NetworkException.ServerError.GatewayTimeout]
 * - [AppError.CustomError]
 */
fun mapNetworkRequestException(e: Exception): AppError {
    return when (e) {
        is HttpStatusCodeError -> when (e.value) {
            400 -> AppError.NetworkException.RequestError.BadRequest(e.message, e)
            401 -> AppError.NetworkException.RequestError.Unauthorized(e.message, e)
            403 -> AppError.NetworkException.RequestError.Forbidden(e.message, e)
            500 -> AppError.NetworkException.ServerError.InternalServerError(e.message, e)
            502 -> AppError.NetworkException.ServerError.BadGateway(e.message, e)
            503 -> AppError.NetworkException.ServerError.ServiceUnavailable(e.message, e)
            504 -> AppError.NetworkException.ServerError.GatewayTimeout(e.message, e)
            else -> AppError.CustomError("Неизвестная ошибка", e)
        }

        else -> AppError.CustomError(error = e)
    }
}

class HttpStatusCodeError(val value: Int) : Exception()
