package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet

@Dao
interface WalletDao {

    @Query("select * from wallets")
    fun getAll(): LiveData<List<Wallet>>

    @Query("select * from wallets where id = :walletId")
    fun getById(walletId: Int): LiveData<Wallet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallets: List<Wallet>)

    @Update()
    fun update(wallet: Wallet)

    @Query("update wallets set balance = balance + :transactionCost where id = :walletId")
    fun executeTransaction(walletId: Int, transactionCost: Double)

    @Delete()
    fun delete(wallet: Wallet)
}

