package ru.yandex.moykoshelek.data.datasource.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDataDao
import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData

@Database(entities = [TransactionData::class, WalletData::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val transactionDataDao: TransactionDataDao
    abstract val walletDataDao: WalletDataDao

}