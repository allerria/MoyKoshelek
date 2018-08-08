package ru.yandex.moykoshelek.data.datasource.local.entities

import android.arch.persistence.room.*
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import java.util.*

@Entity(tableName = "template_transactions", indices = [Index(value = ["name"], unique = true)])
data class TemplateTransaction(@PrimaryKey(autoGenerate = true) var id: Int,
                               @ColumnInfo(name = "name") var name: String,
                               @ColumnInfo(name = "period") var period: Int?,
                               @ColumnInfo(name = "created_at") var time: Date,
                               @ColumnInfo(name = "cost") var cost: Double,
                               @ColumnInfo(name = "currency") var currency: Int,
                               @ColumnInfo(name = "placeholder") var placeholder: String,
                               @ColumnInfo(name = "type") var type: Int,
                               @ColumnInfo(name = "wallet_id") var walletId: Int,
                               @ColumnInfo(name = "category") var category: String
) {
    constructor() : this(0, "template", null, getCurrentDateTime(), 0.0, CurrencyTypes.RUB, "", TransactionTypes.IN, 0, "")
}