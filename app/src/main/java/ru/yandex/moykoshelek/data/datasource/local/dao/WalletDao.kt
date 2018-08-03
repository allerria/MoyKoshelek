package ru.yandex.moykoshelek.data.datasource.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet

@Dao
interface WalletDao {

    @Query("SELECT * from wallets")
    fun getAll(): LiveData<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Update
    fun update(wallet: Wallet)

}

suspend fun WalletDao.getWallets(): LiveData<List<Wallet>> = withContext(DefaultDispatcher) { getAll() }
