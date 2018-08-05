package ru.yandex.moykoshelek.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.local.dao.PeriodTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.di.modules.DatabaseModule
import ru.yandex.moykoshelek.di.modules.NavigationModule
import ru.yandex.moykoshelek.di.modules.ViewModelModule
import javax.inject.Singleton

@Module(includes = [NavigationModule::class, DatabaseModule::class, ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideWalletRepository(walletDao: WalletDao): WalletRepository = WalletRepository(walletDao)

    @Provides
    @Singleton
    fun provideTransactionsRepository(transactionDao: TransactionDao, periodTransactionDao: PeriodTransactionDao): TransactionsRepository = TransactionsRepository(transactionDao, periodTransactionDao)

    @Provides
    @Singleton
    fun provideCurrencyRateRepository(currencyPref: CurrencyPref): CurrencyRateRepository = CurrencyRateRepository(currencyPref)

    @Provides
    @Singleton
    fun provideCurrencyPref(app: Application): CurrencyPref = CurrencyPref(app)

}