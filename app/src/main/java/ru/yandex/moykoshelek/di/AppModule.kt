package ru.yandex.moykoshelek.di

import android.app.Application
import android.preference.PreferenceManager.getDefaultSharedPreferences
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.local.dao.PeriodTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TemplateTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.remote.CurrencyRateRemote
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
    fun provideTransactionsRepository(
            transactionDao: TransactionDao,
            periodTransactionDao: PeriodTransactionDao,
            templateTransactionDao: TemplateTransactionDao
    ): TransactionsRepository = TransactionsRepository(transactionDao, periodTransactionDao, templateTransactionDao)

    @Provides
    @Singleton
    fun provideCurrencyRateRepository(currencyPref: CurrencyPref, currencyRateRemote: CurrencyRateRemote): CurrencyRateRepository = CurrencyRateRepository(currencyPref, currencyRateRemote)

    @Provides
    @Singleton
    fun provideCurrencyPref(app: Application): CurrencyPref = CurrencyPref(getDefaultSharedPreferences(app))

    @Provides
    @Singleton
    fun provideCurrencyRateRemote(): CurrencyRateRemote = CurrencyRateRemote()

}