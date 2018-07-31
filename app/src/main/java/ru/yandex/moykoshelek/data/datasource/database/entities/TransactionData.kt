package ru.yandex.moykoshelek.data.datasource.database.entities

import android.arch.persistence.room.*
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.toString

@Entity(tableName = "transactions", foreignKeys =
[
    (ForeignKey(
                entity = WalletData::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("wallet_id"),
                onDelete = ForeignKey.CASCADE
        ))
], indices = [(Index(value = ["wallet_id"]))])
data class TransactionData(@PrimaryKey(autoGenerate = true) var id: Long?,
                           @ColumnInfo(name = "created_dt") var time: String,
                           @ColumnInfo(name = "cost") var cost: Double,
                           @ColumnInfo(name = "currency") var currency: Int,
                           @ColumnInfo(name = "placeholder") var placeholder: String,
                           @ColumnInfo(name = "type_transaction") var typeTransaction: Int,
                           @ColumnInfo(name = "wallet_id") var walletId: Int?,
                           @ColumnInfo(name = "category") var category: String
){
    constructor():this(null, getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss"),0.0, CurrencyTypes.RUB,"", TransactionTypes.IN, null, "")
}