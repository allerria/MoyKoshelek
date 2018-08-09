package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction

@Dao
interface PeriodTransactionDao {

    @Query("select * from period_transactions")
    fun getAll(): List<PeriodTransaction>

    @Query("select * from period_transactions where id = :periodTransactionId")
    fun getById(periodTransactionId: Int): PeriodTransaction

    @Insert()
    fun insert(periodTransaction: PeriodTransaction)

    @Delete()
    fun delete(periodTransaction: PeriodTransaction)
}
