package com.hfad.palamarchuksuperapp.feature.bone.domain.repository

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import java.util.Date

interface ExchangeOrderApi {
    fun getExchangesByPage(page: Int): AppResult<List<ExchangeOrder>, AppError.NetworkException>
    fun getExchange(id: Int): AppResult<ExchangeOrder, AppError.NetworkException>
    fun getExchangesWithRange(
        from: Date,
        to: Date,
    ): AppResult<List<ExchangeOrder>, AppError.NetworkException>
}