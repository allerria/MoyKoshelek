package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import java.util.*

@Dao
interface TransactionDao {

    @Query("select * from transactions order by created_at desc")
    fun getAll(): LiveData<List<Transaction>>

    @Query("select * from transactions where created_at between :from and :to order by created_at desc")
    fun getAll(from: Date, to: Date): LiveData<List<Transaction>>

    @Query("select * from transactions where id = :transactionId")
    fun getById(transactionId: Int): LiveData<Transaction>

    @Query("select * from transactions where wallet_id = :walletId order by created_at desc")
    fun getAllByWalletId(walletId: Int): LiveData<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transactions: List<Transaction>)

    @Update()
    fun update(transaction: Transaction)

    @Delete()
    fun delete(transaction: Transaction)
}
