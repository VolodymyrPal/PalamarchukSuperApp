package com.hfad.palamarchuksuperapp.feature.bone.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hfad.palamarchuksuperapp.feature.bone.data.local.database.DATABASE_SALES
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Currency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.TransactionType
import java.util.Date

@Entity(
    tableName = DATABASE_SALES,
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("orderId")
    ]
)
data class SaleOrderEntity(
    @PrimaryKey
    val id: Int,
    val productName: String,
    val cargoCategory: String,
    val customerName: String,
    val status: SaleStatus,
    val requestDate: String,
    val documentDate: String,
    val companyName: String,
    val commissionPercent: Double,
    val prepayment: Boolean,
    val orderId: Int? = null,
    val vat: Float = 0.20f,
    @Embedded (prefix = "amountCurrency_")
    val amountCurrency: AmountCurrencyEntity = AmountCurrencyEntity(currency = Currency.UAH, amount = 0.0f),
    val billingDate: Date = Date(),
    val transactionType: TransactionType = TransactionType.DEBIT,
    val versionHash: String = "",
)