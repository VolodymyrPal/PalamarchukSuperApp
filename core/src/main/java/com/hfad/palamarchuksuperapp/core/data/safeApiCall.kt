package com.hfad.palamarchuksuperapp.core.data

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.net.SocketException
import java.nio.channels.UnresolvedAddressException


suspend fun <T> safeApiCall(call: suspend () -> AppResult<T, AppError>): AppResult<T, AppError> {
    return try {
        call()
    } catch (e: Throwable) {
        AppResult.Error(mapApiExceptionToAppError(e))
    }
}

suspend fun <T> safeApiCall(
    call: suspend () -> T
): AppResult<T, AppError> {
    return try {
        AppResult.Success(call())
    } catch (e: Throwable) {
        AppResult.Error(mapApiExceptionToAppError(e))
    }
}

internal fun mapApiExceptionToAppError(e: Throwable): AppError {
    return when (e) {
        is UnresolvedAddressException -> AppError.NetworkException.ApiError.UndefinedError(
            message = "Problem with internet connection.",
            cause = e
        )
        is JsonConvertException -> AppError.NetworkException.ApiError.UndefinedError(
            message = "Problem with parsing response. Please contact developer.",
            cause = e
        )
        is ClosedReceiveChannelException, is SocketException -> AppError.NetworkException.ApiError.UndefinedError(
            message = "Waiting for response timeout. Please contact developer.",
            cause = e
        )
        else -> AppError.NetworkException.ApiError.UndefinedError(
            message = "Unexpected error: ${e.message}",
            cause = e
        )
    }
}