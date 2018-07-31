package ru.yandex.moykoshelek.data.datasource.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.yandex.moykoshelek.data.datasource.database.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.database.dao.WalletDataDao
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData

@Database(entities = [TransactionData::class, WalletData::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val transactionDataDao: TransactionDataDao
    abstract val walletDataDao: WalletDataDao

}