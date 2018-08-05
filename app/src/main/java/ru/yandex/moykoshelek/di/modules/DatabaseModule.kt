package ru.yandex.moykoshelek.di.modules

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.AppDatabase
import ru.yandex.moykoshelek.data.datasource.local.dao.PeriodTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import javax.inject.Singleton
import javax.security.auth.callback.Callback

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(app: Application) =
            Room.databaseBuilder(app, AppDatabase::class.java, "app.db")
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides
    @Singleton
    fun provideWalletDao(db: AppDatabase): WalletDao = db.walletDao

    @Provides
    @Singleton
    fun provideTransactionsDao(db: AppDatabase): TransactionDao = db.transactionDao

    @Provides
    @Singleton
    fun providePeriodTransactionsDao(db: AppDatabase): PeriodTransactionDao = db.periodTransactionDao
}