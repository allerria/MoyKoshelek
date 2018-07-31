package ru.yandex.moykoshelek.di.modules

import dagger.Module
import dagger.Provides
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

@Module
class BalanceModule {
    @Provides
    fun provideWalletInteractor(
            walletRepository: WalletRepository,
            transactionsRepository: TransactionsRepository,
            currencyRateRepository: CurrencyRateRepository
    ): WalletInteractor = WalletInteractor(walletRepository, transactionsRepository, currencyRateRepository)
}