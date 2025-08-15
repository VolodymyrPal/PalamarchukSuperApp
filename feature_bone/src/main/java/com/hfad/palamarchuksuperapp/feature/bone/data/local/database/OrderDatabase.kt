package com.hfad.palamarchuksuperapp.feature.bone.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.feature.bone.data.local.converters.DateConverter
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.PaymentOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.RemoteKeysDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.SaleOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.CashPaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ExchangeOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentStatisticEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SaleOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SalesStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.OrderRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.PaymentRemoteKeys
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.keys.SaleRemoteKeys

@Database(
    entities = [
        OrderEntity::class,
        OrderRemoteKeys::class,
        OrderStatisticsEntity::class,
        ServiceOrderEntity::class,
        SaleOrderEntity::class,
        SaleRemoteKeys::class,
        SalesStatisticsEntity::class,
        PaymentOrderEntity::class,
        PaymentRemoteKeys::class,
        PaymentStatisticEntity::class,
        ExchangeOrderEntity::class,
        CashPaymentOrderEntity::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class BoneDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun saleOrderDao(): SaleOrderDao
    abstract fun paymentOrderDao(): PaymentOrderDao
//    abstract fun financeOperationDao(): FinanceOperationDao
}