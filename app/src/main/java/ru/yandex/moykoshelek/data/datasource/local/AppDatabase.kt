package ru.yandex.moykoshelek.data.datasource.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet

@Database(entities = [Transaction::class, Wallet::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao
    abstract val walletDao: WalletDao

}