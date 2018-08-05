package ru.yandex.moykoshelek.data.datasource.local.entities

import android.arch.persistence.room.*
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.toString
import java.util.*

@Entity(tableName = "period_transactions")
data class PeriodTransaction(@PrimaryKey(autoGenerate = true) var id: Int,
                             @ColumnInfo(name = "start_at") var time: Date,
                             @ColumnInfo(name = "period_days") var period: Int
) {
    constructor() : this(0, getCurrentDateTime(), 30)
}