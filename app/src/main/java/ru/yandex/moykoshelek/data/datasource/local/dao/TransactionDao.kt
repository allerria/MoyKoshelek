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

    @Query("SELECT DISTINCT category FROM transactions")
    fun getCategories(): LiveData<List<String>>

    @Insert()
    fun insert(transaction: Transaction)
}

suspend fun TransactionDao.getWallets(): LiveData<List<Transaction>> = withContext(DefaultDispatcher) { getAll() }
