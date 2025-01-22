package com.hfad.palamarchuksuperapp.domain.models

import android.database.SQLException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

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
     * Represents an unknown database error.
     *
     * @param message Detailed error message (optional).
     * @param cause Root cause of the error, if available (optional).
     */
    class UnknownException(
        message: String? = null,
        cause: Throwable,
    ) : DatabaseError(message, cause)

    /**
     * Represents a general SQL error or other database-related issue.
     *
     * @param message Detailed error message (optional).
     * @param cause Root cause of the error, if available (optional).
     */
    class SQLException(
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

/**
 * Represents a custom exception wrapper for database errors.
 *
 * @property error The specific database error instance.
 */
class DatabaseException(val error: DatabaseError) : Exception(error.message, error.cause)


/** Safe wrapper for database calls with retries.
 *
 * This function executes a database operation wrapped in a suspend block and handles potential exceptions with retries.
 *
 * @param retries The maximum number of retries to attempt the database operation in case of
 * `android.database.SQLException`. Defaults to 3.
 * @param block The suspend block containing the database operation to be executed.
 * This block should return the result of the operation (type `T`).
 *
 * @return A `Result<T, AppError.DatabaseError>` representing the outcome of the database operation.
 * With result error possible error: [mapSQLException]
 */
suspend fun <T> safeDbCallWithRetries(
    retries: Int = 3,
    block: suspend () -> T,
): Result<T, DatabaseError> {

    var retryCount = 0

    return withContext(Dispatchers.IO) {
        try {
            Result.Success<T, DatabaseError>(block())
        } catch (e: SQLException) {
            while (retryCount < retries) {
                Log.w(
                    "Database",
                    "Attempt $retryCount to perform database operation failed. Retrying..."
                )
                delay(500)
                retryCount++
            }
            Result.Error(error = mapSQLException(e))
        } catch (e: Exception) {
            Result.Error(
                DatabaseError.UnknownException(
                    "Unexpected exception: ${e.message}",
                    e
                )
            )
        }
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
 * - [DatabaseError.SQLException]
 */
fun mapSQLException(e: SQLException): DatabaseError {
    return when (e) {
        is SQLiteConstraintException -> DatabaseError.ConstraintViolation(e.message, e)
        is SQLiteDiskIOException -> DatabaseError.DiskIOException(e.message, e)
        is SQLiteFullException -> DatabaseError.OutOfMemoryException(e.message, e)
        else -> DatabaseError.SQLException(e.message, e)
    }
}