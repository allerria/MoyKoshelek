package ru.yandex.moykoshelek.di.modules

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.datasource.local.AppDatabase
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDataDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "app.db")
                    .build()

    @Provides
    @Singleton
    fun provideWalletDao(db: AppDatabase): WalletDataDao = db.walletDataDao

    @Provides
    @Singleton
    fun provideTransactionsDao(db: AppDatabase): TransactionDataDao = db.transactionDataDao

}