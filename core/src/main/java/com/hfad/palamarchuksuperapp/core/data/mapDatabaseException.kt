package com.hfad.palamarchuksuperapp.core.data

import android.database.SQLException
import android.database.sqlite.SQLiteAccessPermException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteReadOnlyDatabaseException
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.AppError.DatabaseError

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
        // Constraint violation (e.g., unique constraint, foreign key constraint)
        is SQLiteConstraintException -> DatabaseError.ConstraintViolation(
            message = "Constraint violation occurred: ${e.message}",
            cause = e
        )

        // Disk I/O errors (e.g., issues with reading or writing to disk)
        is SQLiteDiskIOException -> DatabaseError.DiskIOException(
            message = "Disk I/O error occurred: ${e.message}",
            cause = e
        )

        // Out of memory errors (e.g., disk space full)
        is SQLiteFullException -> DatabaseError.OutOfMemoryException(
            message = "Database operation failed due to insufficient memory or disk space: ${e.message}",
            cause = e
        )

        // Access permission errors (e.g., insufficient permissions to access the database file)
        is SQLiteAccessPermException -> DatabaseError.AccessPermissionError(
            message = "Database access permission error: ${e.message}",
            cause = e
        )

        // Read-only database errors (e.g., trying to modify a read-only database)
        is SQLiteReadOnlyDatabaseException -> DatabaseError.ReadOnlyDatabaseError(
            message = "Database is in read-only mode: ${e.message}",
            cause = e
        )

        // Database corruption errors (e.g., corrupted database file)
        is SQLiteDatabaseCorruptException -> DatabaseError.DatabaseCorruptionError(
            message = "Database file is corrupted: ${e.message}",
            cause = e
        )

        // Default case: unknown or unhandled exception
        else -> {
            // Log the unexpected exception for debugging purposes (use your preferred logging framework)
            println("Unhandled SQL exception: ${e.message}")
            DatabaseError.UnhandledSQLException(e.message, e)
        }
    }
}