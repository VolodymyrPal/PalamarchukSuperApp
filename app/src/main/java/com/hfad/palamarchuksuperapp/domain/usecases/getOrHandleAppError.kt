package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult

inline fun <T> AppResult<T, AppError>.getOrHandleAppError(
    onError: (AppError) -> Nothing,
): T {
    return when (this) {
        is AppResult.Success -> this.data
        is AppResult.Error -> onError(this.error)
    }
}