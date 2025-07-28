package com.hfad.palamarchuksuperapp.core.domain

typealias RootError = Error

sealed interface AppResult<out D, out E : AppError> {
    data class Success<out D, out E : AppError>(val data: D, val error: E? = null) : AppResult<D, E>
    data class Error<out D, out E : AppError>(val error: E, val data: D? = null) : AppResult<D, E>
}