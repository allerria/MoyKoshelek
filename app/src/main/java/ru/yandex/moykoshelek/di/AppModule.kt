package ru.yandex.moykoshelek.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.MoyKoshelekApp
import ru.yandex.moykoshelek.data.datasource.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.database.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.database.dao.WalletDataDao
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.di.modules.DatabaseModule
import ru.yandex.moykoshelek.di.modules.NavigationModule

@Module(includes = [NavigationModule::class, DatabaseModule::class])
class AppModule {

    @Provides
    fun provideWalletRepository(walletDataDao: WalletDataDao): WalletRepository = WalletRepository(walletDataDao)

    @Provides
    fun provideTransactionsRepository(transactionDataDao: TransactionDataDao): TransactionsRepository = TransactionsRepository(transactionDataDao)

    @Provides
    fun provideCurrencyRateRepository(currencyPref: CurrencyPref): CurrencyRateRepository = CurrencyRateRepository(currencyPref)

    @Provides
    fun provideCurrencyPref(app: Application): CurrencyPref = CurrencyPref(app)

}