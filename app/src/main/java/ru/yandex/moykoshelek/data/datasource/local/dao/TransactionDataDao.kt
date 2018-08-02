package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData

@Dao
interface TransactionDataDao {

    @Query("SELECT * from transactions")
    fun getAll(): LiveData<List<TransactionData>>

    @Query("SELECT DISTINCT category FROM transactions")
    fun getCategories(): LiveData<List<String>>

    @Insert()
    fun insert(transactionData: TransactionData)
}