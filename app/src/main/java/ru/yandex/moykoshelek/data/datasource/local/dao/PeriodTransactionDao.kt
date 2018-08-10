package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction

@Dao
interface PeriodTransactionDao {

    @Query("select * from period_transactions order by last_bill_date desc")
    fun getAll(): LiveData<List<PeriodTransaction>>

    @Query("select * from period_transactions where id = :periodTransactionId")
    fun getById(periodTransactionId: Int): PeriodTransaction

    @Insert()
    fun insert(periodTransaction: PeriodTransaction)

    @Update()
    fun update(periodTransaction: PeriodTransaction)

    @Delete()
    fun delete(periodTransaction: PeriodTransaction)
}
