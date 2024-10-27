package com.hfad.palamarchuksuperapp.domain.models

sealed interface AppError : Error {
    interface Network : AppError {
        enum class RequestError: Network {
            BadRequest, //400
            Unauthorized, //401
            Forbidden, //403 лимиты, ограничения по IP
            NotFound, //404
        }
        enum class ServerError: Network {
            InternalServerError, //500
            BadGateway, //502
            ServiceUnavailable, //503
            GatewayTimeout, //504
        }

    }
    enum class LocalData : AppError {
        DatabaseError, // ошибка базы данных
    }

    enum class OtherErrors : AppError {
        NotImplemented, // не реализовано
        Unknown, // неизвестная ошибка
    }

    data class CustomError (val errorText: String? = null, val error: Throwable? = null): AppError

}