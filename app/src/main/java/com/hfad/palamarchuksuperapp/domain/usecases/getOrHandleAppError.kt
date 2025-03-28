package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.Result

inline fun <T> Result<T, AppError>.getOrHandleAppError(
    onError: (AppError) -> Nothing,
): T {
    return when (this) {
        is Result.Success -> this.data
        is Result.Error -> onError(this.error)
    }
}