package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction

@Dao
interface TemplateTransactionDao {

    @Query("select * from template_transactions")
    fun getAll(): LiveData<List<TemplateTransaction>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(templateTransaction: TemplateTransaction)
}
