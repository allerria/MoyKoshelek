package ru.yandex.moykoshelek.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.yandex.moykoshelek.balance.BalanceFragment
import ru.yandex.moykoshelek.balance.BalanceModule
import ru.yandex.moykoshelek.menu.MenuFragment
import ru.yandex.moykoshelek.transaction.AddTransactionFragment
import ru.yandex.moykoshelek.transaction.AddTransactionModule
import ru.yandex.moykoshelek.wallet.AddWalletFragment
import ru.yandex.moykoshelek.wallet.AddWalletModule

@Module
abstract class MainModule {

    @ContributesAndroidInjector(modules = [BalanceModule::class])
    abstract fun bindBalanceFragment(): BalanceFragment

    @ContributesAndroidInjector()
    abstract fun bindMenuFragment(): MenuFragment

    @ContributesAndroidInjector(modules = [AddWalletModule::class])
    abstract fun bindAddWalletFragment(): AddWalletFragment

    @ContributesAndroidInjector(modules = [AddTransactionModule::class])
    abstract fun bindAddTransactionFragment(): AddTransactionFragment

}