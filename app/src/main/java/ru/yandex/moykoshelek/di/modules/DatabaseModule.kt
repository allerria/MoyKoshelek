package ru.yandex.moykoshelek.di.modules

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.datasource.database.AppDatabase
import ru.yandex.moykoshelek.data.datasource.database.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.database.dao.WalletDataDao
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData

@Module
class DatabaseModule {

    @Provides
    fun provideDb(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "app.db")
                    .build()

    @Provides
    fun provideWalletDao(db: AppDatabase): WalletDataDao = db.walletDataDao

    @Provides
    fun provideTransactionsDao(db: AppDatabase): TransactionDataDao = db.transactionDataDao

}