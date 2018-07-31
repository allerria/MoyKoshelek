package ru.yandex.moykoshelek.data.datasource.database.entities

import android.arch.persistence.room.*

@Entity(tableName = "wallets")
data class WalletData(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "type") var type: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "balance") var balance: Double,
                      @ColumnInfo(name = "currency") var currency: Int,
                      @ColumnInfo(name = "number") var number: String,
                      @ColumnInfo(name = "date") var date: String

){
    constructor():this(null, 0, "",0.0,0,"","12/12")
}