package com.mad.cube.transaction

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "transactions")
@Parcelize
data class Transaction(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "description")
    val description: String
): Parcelable