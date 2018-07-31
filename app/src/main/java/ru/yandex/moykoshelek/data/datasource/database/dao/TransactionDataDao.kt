package ru.yandex.moykoshelek.data.datasource.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData

@Dao
interface TransactionDataDao {

    @Query("SELECT * from transactions")
    fun getAll(): List<TransactionData>

    @Query("SELECT DISTINCT category FROM transactions")
    fun getCategories(): List<String>

    @Insert()
    fun insert(weatherData: TransactionData)
}