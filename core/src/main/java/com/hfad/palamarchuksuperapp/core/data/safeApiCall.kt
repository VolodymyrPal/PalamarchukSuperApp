package com.hfad.palamarchuksuperapp.core.data

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import javax.net.ssl.SSLException


suspend fun <T> safeApiCall(call: suspend () -> AppResult<T, AppError>): AppResult<T, AppError> {
    return try {
        call()
    } catch (e: Throwable) {
        AppResult.Error(mapApiExceptionToAppError(e))
    }
}

suspend fun <T> tryApiRequest(
    block: suspend () -> T,
): AppResult<T, AppError> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(mapApiException(e))
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

fun mapApiException(e: Exception): AppError.NetworkException.ApiError {

    fun customError(message: String) =
        AppError.NetworkException.ApiError.CustomApiError(message, e)

    return when (e) {
        // HTTP client/server errors
        is ClientRequestException -> {
            val status = e.response.status.value
            when (status) {
                400 -> AppError.NetworkException.ApiError.BadApi(
                    message = "Bad request: ${e.message}", cause = e
                )

                401 -> AppError.NetworkException.ApiError.Unauthorized(
                    message = "Unauthorized access: ${e.message}", cause = e
                )

                403 -> AppError.NetworkException.ApiError.Forbidden(
                    message = "Access forbidden: ${e.message}", cause = e
                )

                404 -> AppError.NetworkException.ApiError.NotFound(
                    message = "Resource not found: ${e.message}", cause = e
                )
//                429 -> AppError.NetworkException.ApiError.TooManyRequests(
//                    message = "Too many requests: ${e.message}", cause = e
//                )
                else -> customError("HTTP error ($status): ${e.message}")
            }
        }

        is ServerResponseException -> {
            val status = e.response.status.value
            when (status) {
                500 -> AppError.NetworkException.ApiError.InternalApiError(
                    message = "Internal server error: ${e.message}", cause = e
                )
                502 -> AppError.NetworkException.ApiError.BadGateway(
                    message = "Bad gateway: ${e.message}", cause = e
                )
                503 -> AppError.NetworkException.ApiError.ServiceUnavailable(
                    message = "Service unavailable: ${e.message}", cause = e
                )
                504 -> AppError.NetworkException.ApiError.GatewayTimeout(
                    message = "Gateway timeout: ${e.message}", cause = e
                )
                else -> customError("Server error ($status): ${e.message}")
            }
        }

        // Network connectivity issues
        is ConnectException -> AppError.NetworkException.ApiError.ServiceUnavailable(
            message = "Unable to connect to server: ${e.message}", cause = e
        )

        is SocketTimeoutException -> AppError.NetworkException.ApiError.GatewayTimeout(
            message = "Request timeout: ${e.message}", cause = e
        )

        is UnknownHostException -> AppError.NetworkException.ApiError.ServiceUnavailable(
            message = "Unknown host: ${e.message}", cause = e
        )

        // SSL/TLS errors
        is SSLException -> customError("SSL/TLS error: ${e.message}")

        // I/O errors
        is IOException -> AppError.NetworkException.ApiError.ServiceUnavailable(
            message = "Network I/O error: ${e.message}", cause = e
        )

        // Default: unknown/unexpected exception
        else -> {
            println("Unhandled API exception: ${e.message}")
            AppError.NetworkException.ApiError.UndefinedError(
                message = "Unexpected error: ${e.message}",
                cause = e
            )
        }
    }
}
