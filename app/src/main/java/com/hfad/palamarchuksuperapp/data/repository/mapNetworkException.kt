package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.AppError.CustomError
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.BadApi
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.Forbidden
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.Unauthorized
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.BadGateway
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.GatewayTimeout
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.InternalApiError
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ApiError.ServiceUnavailable

/** Maps an [Exception] to a specific [AppError.NetworkException] subtype.
 *
 * @param e the Exception to be mapped.
 *
 * @return A specific subtype of [AppError.NetworkException] corresponding to the given Exception.
 * Possible outcomes include:
 * - [AppError.NetworkException.ApiError.BadApi]
 * - [AppError.NetworkException.ApiError.Unauthorized]
 * - [AppError.NetworkException.ApiError.Forbidden]
 * - [AppError.NetworkException.ApiError.InternalApiError]
 * - [AppError.NetworkException.ApiError.BadGateway]
 * - [AppError.NetworkException.ApiError.ServiceUnavailable]
 * - [AppError.NetworkException.ApiError.GatewayTimeout]
 * - [AppError.CustomError]
 */
fun mapNetworkCodeToException(e: Exception): AppError {
    return when (e) {
        is HttpStatusCodeError -> when (e.value) {
            400 -> BadApi(e.message, e)
            401 -> Unauthorized(e.message, e)
            403 -> Forbidden(e.message, e)
            500 -> InternalApiError(e.message, e)
            502 -> BadGateway(e.message, e)
            503 -> ServiceUnavailable(e.message, e)
            504 -> GatewayTimeout(e.message, e)
            else -> CustomError("Неизвестная ошибка", e)
        }

        else -> CustomError(cause = e)
    }
}

class HttpStatusCodeError(val value: Int) : Exception()