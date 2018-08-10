package ru.yandex.moykoshelek.data.datasource.local.entities

import android.arch.persistence.room.*

@Entity(tableName = "wallets")
data class Wallet(@PrimaryKey(autoGenerate = true) var id: Int,
                  @ColumnInfo(name = "name") var name: String,
                  @ColumnInfo(name = "balance") var balance: Double,
                  @ColumnInfo(name = "currency") var currency: Int
) {
    constructor() : this(0, "", 0.0, 0)
}