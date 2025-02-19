package com.hfad.palamarchuksuperapp.domain.models

sealed interface AppError : Error {

    data class CustomError(val errorText: String? = null, val error: Throwable? = null) : AppError

    sealed interface NetworkException : AppError {

        val message: String?
        val cause: Throwable?

        /**
         * Represents a sealed hierarchy of request-related errors.
         * Each subclass corresponds to a specific type of request error.
         *
         * @property message A detailed error message (optional).
         * @property cause The root cause of the error, if available (optional).
         */
        sealed class ApiError(
            override val message: String? = null,
            override val cause: Throwable? = null,
        ) : NetworkException {
            class BadApi(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause) //400

            class Unauthorized(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause) //401

            class Forbidden(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause) //403 лимиты, ограничения по IP

            class NotFound(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause) //404

            class UndefinedError(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause) // undefined error

            class InternalApiError(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause)//500

            class BadGateway(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause)//502

            class ServiceUnavailable(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause)//503

            class GatewayTimeout(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause)//504

            class CustomApiError(message: String? = null, cause: Throwable? = null) :
                ApiError(message, cause)// undefined error
        }

    }

    /**
     * Represents a sealed hierarchy of database-related errors.
     * Each subclass corresponds to a specific type of database error.
     *
     * @property message A detailed error message (optional).
     * @property cause The root cause of the error, if available (optional).
     */

    sealed class DatabaseError(
        val message: String? = null, val cause: Throwable? = null,
    ) : AppError {

        /**
         * Represents a general SQL error or other database-related issue.
         *
         * This error is used as a fallback for exceptions that do not match other specific error types.
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

        /**
         * Represents a permission error, such as an attempt to access a database file without proper permissions.
         *
         * This error occurs when the database file is inaccessible due to file system permissions.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class AccessPermissionError(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /**
         * Represents an error caused by attempting to modify a database in read-only mode.
         *
         * This error occurs when a write operation is attempted on a read-only database connection.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class ReadOnlyDatabaseError(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)

        /**
         * Represents a database corruption error.
         *
         * This error is triggered when the database file is found to be corrupted, making operations unreliable.
         *
         * @param message Detailed error message (optional).
         * @param cause Root cause of the error, if available (optional).
         */
        class DatabaseCorruptionError(
            message: String? = null,
            cause: Throwable? = null,
        ) : DatabaseError(message, cause)
    }
}
