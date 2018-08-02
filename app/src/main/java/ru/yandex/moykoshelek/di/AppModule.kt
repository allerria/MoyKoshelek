package ru.yandex.moykoshelek.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDataDao
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
    fun provideWalletRepository(walletDataDao: WalletDataDao): WalletRepository = WalletRepository(walletDataDao)

    @Provides
    @Singleton
    fun provideTransactionsRepository(transactionDataDao: TransactionDataDao): TransactionsRepository = TransactionsRepository(transactionDataDao)

    @Provides
    @Singleton
    fun provideCurrencyRateRepository(currencyPref: CurrencyPref): CurrencyRateRepository = CurrencyRateRepository(currencyPref)

    @Provides
    @Singleton
    fun provideCurrencyPref(app: Application): CurrencyPref = CurrencyPref(app)

}