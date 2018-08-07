package ru.yandex.moykoshelek.data.datasource.local.entities

import android.arch.persistence.room.*
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import java.util.*

@Entity(tableName = "period_transactions")
data class PeriodTransaction(@PrimaryKey(autoGenerate = true) var id: Int,
                             @ColumnInfo(name = "last_bill_date") var time: Date,
                             @ColumnInfo(name = "period_days") var period: Int,
                             @ColumnInfo(name = "cost") var cost: Double,
                             @ColumnInfo(name = "currency") var currency: Int,
                             @ColumnInfo(name = "placeholder") var placeholder: String,
                             @ColumnInfo(name = "type") var type: Int,
                             @ColumnInfo(name = "wallet_id") var walletId: Int,
                             @ColumnInfo(name = "category") var category: String
) {
    constructor() : this(0, getCurrentDateTime(), 30, 0.0, CurrencyTypes.RUB, "", TransactionTypes.IN, 0, "")
}