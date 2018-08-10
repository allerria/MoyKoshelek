package ru.yandex.moykoshelek.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.yandex.moykoshelek.ui.about.AboutFragment
import ru.yandex.moykoshelek.ui.balance.BalanceFragment
import ru.yandex.moykoshelek.ui.menu.MenuFragment
import ru.yandex.moykoshelek.ui.settings.SettingsFragment
import ru.yandex.moykoshelek.ui.transaction.TransactionFragment
import ru.yandex.moykoshelek.ui.transaction.TransactionsFragment
import ru.yandex.moykoshelek.ui.wallet.WalletFragment
import ru.yandex.moykoshelek.ui.wallet.WalletsFragment

@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun bindBalanceFragment(): BalanceFragment

    @ContributesAndroidInjector()
    abstract fun bindMenuFragment(): MenuFragment

    @ContributesAndroidInjector
    abstract fun bindAddWalletFragment(): WalletFragment

    @ContributesAndroidInjector
    abstract fun bindAddTransactionFragment(): TransactionFragment

    @ContributesAndroidInjector
    abstract fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun bindWalletsFragment(): WalletsFragment

    @ContributesAndroidInjector
    abstract fun bindTransactionsFragment(): TransactionsFragment

}