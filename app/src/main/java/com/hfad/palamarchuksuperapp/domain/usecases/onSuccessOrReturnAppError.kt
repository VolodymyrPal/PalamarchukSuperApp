package com.hfad.palamarchuksuperapp.domain.usecases

import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.models.Result

inline fun <T> Result<T, AppError>.onSuccessOrReturnAppError(
    onError: (AppError) -> Nothing,
): T {
    return when (this) {
        is Result.Success -> this.data
        is Result.Error -> onError(this.error)
    }
}