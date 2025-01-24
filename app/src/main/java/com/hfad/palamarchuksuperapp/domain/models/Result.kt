package com.hfad.palamarchuksuperapp.domain.models

typealias RootError = Error

sealed interface Result<out D, out E : AppError> {
    data class Success<out D, out E : AppError>(val data: D) : Result<D, E>
    data class Error<out D, out E : AppError>(val error: E, val data: D? = null) : Result<D, E>
}