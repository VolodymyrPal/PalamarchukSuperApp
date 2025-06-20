package com.hfad.palamarchuksuperapp.core.data

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.net.SocketException
import java.nio.channels.UnresolvedAddressException
import io.ktor.serialization.JsonConvertException


suspend fun <T> safeApiCall(call: suspend () -> AppResult<T, AppError>): AppResult<T, AppError> {
    return try {
        call()
    } catch (e: UnresolvedAddressException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Problem with internet connection.",
                cause = e
            )
        )
    } catch (e: JsonConvertException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Problem with parsing response. Please contact developer.",
                cause = e
            )
        )
    } catch (e: ClosedReceiveChannelException) {
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Waiting for response timeout. Please contact developer.",
                cause = e
            )
        )
    } catch (e: SocketException) { //Sometimes when bad connection occur
        AppResult.Error(
            error = AppError.NetworkException.ApiError.UndefinedError(
                message = "Waiting for response timeout. Please contact developer.",
                cause = e
            )
        )
    }
}