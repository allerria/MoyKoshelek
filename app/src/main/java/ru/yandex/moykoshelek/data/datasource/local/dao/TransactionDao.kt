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

    @Query("select * from transactions order by created_at desc")
    fun getAll(): LiveData<List<Transaction>>

    @Query("select * from transactions where wallet_id = :walletId order by created_at desc")
    fun getAllByWalletId(walletId: Int): LiveData<List<Transaction>>

    @Insert()
    fun insert(transaction: Transaction)

    @Insert()
    fun insert(transactions: List<Transaction>)
}
