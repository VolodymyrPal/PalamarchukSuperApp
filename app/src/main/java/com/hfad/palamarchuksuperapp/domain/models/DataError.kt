package com.hfad.palamarchuksuperapp.domain.models

sealed interface DataError : Error {
    enum class Network : DataError {
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
    enum class Local : DataError {
        DatabaseError, // ошибка базы данных
        Unknown // неизвестная ошибка
    }
}