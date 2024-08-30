package com.hfad.palamarchuksuperapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hfad.palamarchuksuperapp.data.dao.StoreDao
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW

@Database(entities = [ProductDomainRW::class], version = 1, exportSchema = true)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}

const val DATABASE_MAIN_ENTITY_PRODUCT = "storedatabase"

