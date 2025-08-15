package com.hfad.palamarchuksuperapp.feature.bone.data.local.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.palamarchuksuperapp.feature.bone.data.local.converters.DateConverter
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.ExchangeOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.FinanceOperationDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.GeneralDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.OrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.PaymentOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.RemoteKeysDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.SaleOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.dao.ServiceOrderDao
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics

@Database(
    entities = [
        OrderEntity::class,
        OrderRemoteKeys::class,
        OrderStatistics::class,
        ServiceOrderEntity::class,
//        ExchangeOrder::class,
//        PaymentOrder::class,
//        PaymentStatistic::class,
//        SaleOrder::class,
//        SalesStatistics::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class BoneDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun remoteKeysDao(): RemoteKeysDao
//    abstract fun exchangeOrderDao(): ExchangeOrderDao
//    abstract fun financeOperationDao(): FinanceOperationDao
//    abstract fun paymentOrderDao(): PaymentOrderDao
//    abstract fun saleOrderDao(): SaleOrderDao
//    abstract fun generalDao(): GeneralDao
//    abstract fun serviceOrderDao(): ServiceOrderDao
}