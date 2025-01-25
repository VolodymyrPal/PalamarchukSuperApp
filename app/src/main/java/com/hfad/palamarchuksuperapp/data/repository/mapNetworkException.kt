package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.AppError.CustomError
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.RequestError.BadRequest
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.RequestError.Forbidden
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.RequestError.Unauthorized
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ServerError.BadGateway
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ServerError.GatewayTimeout
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ServerError.InternalServerError
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException.ServerError.ServiceUnavailable

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
fun mapNetworkCodeToException(e: Exception): AppError {
    return when (e) {
        is HttpStatusCodeError -> when (e.value) {
            400 -> BadRequest(e.message, e)
            401 -> Unauthorized(e.message, e)
            403 -> Forbidden(e.message, e)
            500 -> InternalServerError(e.message, e)
            502 -> BadGateway(e.message, e)
            503 -> ServiceUnavailable(e.message, e)
            504 -> GatewayTimeout(e.message, e)
            else -> CustomError("Неизвестная ошибка", e)
        }

        else -> CustomError(error = e)
    }
}

class HttpStatusCodeError(val value: Int) : Exception()