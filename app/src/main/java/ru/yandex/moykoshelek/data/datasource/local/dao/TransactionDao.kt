package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * from transactions")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * from transactions t1 where id = (select id from transactions where period_transaction_id = t1.period_transaction_id order by created_at desc limit 1)")
    fun getAllLastPeriodTransaction(): List<Transaction>

    @Insert()
    fun insert(transaction: Transaction)
}

suspend fun TransactionDao.getTransactions(): LiveData<List<Transaction>> = withContext(DefaultDispatcher) { getAll() }

suspend fun TransactionDao.getLastPeriodTransactions(): List<Transaction> = withContext(DefaultDispatcher) { getAllLastPeriodTransaction() }

