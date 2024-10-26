package com.hfad.palamarchuksuperapp.domain.models

sealed interface AppError : Error {
    enum class Network : AppError {
        BadRequest, //400
        Unauthorized, //401
        Forbidden, //403 лимиты, ограничения по IP
        NotFound, //404
        InternalServerError, //500
        BadGateway, //502
        ServiceUnavailable, //503
        GatewayTimeout, //504
        Unknown // неизвестная ошибка
    }
    enum class Local : AppError {
        DatabaseError, // ошибка базы данных
        Unknown // неизвестная ошибка
    }
    data class CustomError (val errorText: String? = null, val error: Throwable? = null): AppError

}