package com.hfad.palamarchuksuperapp.feature.bone.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.feature.bone.data.local.converters.DateConverter
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.PaymentOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.RemoteKeysDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.SaleOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.AmountCurrencyListConverter
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SaleOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.PaymentRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.SaleRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.OrderStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.PaymentStatisticEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.SalesStatisticsEntity

@Database(
    entities = [
        // Order
        OrderEntity::class,
        ServiceOrderEntity::class,
        OrderStatisticsEntity::class,
        OrderRemoteKeys::class,

        // Sale
        SaleOrderEntity::class,
        SalesStatisticsEntity::class,
        SaleRemoteKeys::class,

        // Order
        PaymentOrderEntity::class,
        PaymentStatisticEntity::class,
        PaymentRemoteKeys::class,

//        ExchangeOrderEntity::class,
//        CashPaymentOrderEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, AmountCurrencyListConverter::class)
abstract class BoneDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun saleOrderDao(): SaleOrderDao
    abstract fun paymentOrderDao(): PaymentOrderDao
//    abstract fun financeOperationDao(): FinanceOperationDao
}