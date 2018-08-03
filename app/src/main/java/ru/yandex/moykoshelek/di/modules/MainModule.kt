package ru.yandex.moykoshelek.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.yandex.moykoshelek.ui.about.AboutFragment
import ru.yandex.moykoshelek.ui.balance.BalanceFragment
import ru.yandex.moykoshelek.ui.menu.MenuFragment
import ru.yandex.moykoshelek.ui.settings.SettingsFragment
import ru.yandex.moykoshelek.ui.transaction.AddTransactionFragment
import ru.yandex.moykoshelek.ui.wallet.AddWalletFragment

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

    @ContributesAndroidInjector
    abstract fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun bindSettingsFragment(): SettingsFragment

}