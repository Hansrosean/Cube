package com.mad.cube.transaction

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): List<Transaction>

    @Insert
    fun insertTransaction(vararg transactions: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(vararg transaction: Transaction)
}