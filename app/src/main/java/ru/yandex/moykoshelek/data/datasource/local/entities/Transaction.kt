package ru.yandex.moykoshelek.data.datasource.local.entities

import android.arch.persistence.room.*
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import java.util.*

@Entity(tableName = "transactions", indices = [(Index(value = ["wallet_id"]))])
data class Transaction(@PrimaryKey(autoGenerate = true) var id: Int,
                       @ColumnInfo(name = "created_at") var date: Date,
                       @ColumnInfo(name = "cost") var cost: Double,
                       @ColumnInfo(name = "currency") var currency: Int,
                       @ColumnInfo(name = "type") var type: Int,
                       @ColumnInfo(name = "wallet_id") var walletId: Int,
                       @ColumnInfo(name = "category") var category: String
) {
    constructor() : this(0, getCurrentDateTime(), 0.0, CurrencyTypes.RUB, TransactionTypes.IN, 0, "")
}