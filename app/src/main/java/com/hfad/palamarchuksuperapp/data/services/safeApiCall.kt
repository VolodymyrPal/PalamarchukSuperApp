package com.hfad.palamarchuksuperapp.data.services

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.AppError.NetworkException
import com.hfad.palamarchuksuperapp.domain.models.Result
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException

suspend fun <T> safeApiCall(call: suspend () -> Result<T, AppError>): Result<T, AppError> {
    return try {
        call()
    } catch (e: UnresolvedAddressException) {
        Result.Error(
            error = AppError.NetworkException.RequestError.UndefinedError(
                message = "Problem with internet connection.",
                cause = e
            )
        )
    } catch (e: JsonConvertException) {
        Result.Error(
            error = AppError.NetworkException.RequestError.UndefinedError(
                message = "Problem with parsing response. Please contact developer.",
                cause = e
            )
        )
    }
}