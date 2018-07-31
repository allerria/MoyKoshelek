package ru.yandex.moykoshelek.data.datasource.database

import android.content.Context
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import ru.yandex.moykoshelek.data.datasource.database.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.database.dao.WalletDataDao
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData

@Database(entities = [(TransactionData::class), (WalletData::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDataDao(): TransactionDataDao
    abstract fun walletDataDao(): WalletDataDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(TransactionData::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "app.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}