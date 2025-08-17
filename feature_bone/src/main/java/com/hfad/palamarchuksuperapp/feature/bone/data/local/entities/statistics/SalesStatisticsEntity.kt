package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SALES_STATISTICS
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.AmountCurrencyEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency

@Entity(tableName = DATABASE_SALES_STATISTICS)
data class SalesStatisticsEntity(
    @PrimaryKey
    val id: Int = 1,
    @Embedded(prefix = "totalSalesAmount_")
    val totalSalesAmount: AmountCurrencyEntity = AmountCurrencyEntity(currency = Currency.UAH, amount = 0f),
    @Embedded(prefix = "totalSalesNdsAmount_")
    val totalSalesNdsAmount: AmountCurrencyEntity = AmountCurrencyEntity(currency = Currency.UAH, amount = 0f),
    val totalBuyers: Int = 0,
)