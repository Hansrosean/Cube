package com.mad.cube.transaction

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}