package ru.yandex.moykoshelek.data.datasource.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData

@Dao
interface WalletDataDao {

    @Query("SELECT * from wallets")
    fun getAll(): LiveData<List<WalletData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(walletData: WalletData)

    @Update
    fun update(wallet: WalletData)
}