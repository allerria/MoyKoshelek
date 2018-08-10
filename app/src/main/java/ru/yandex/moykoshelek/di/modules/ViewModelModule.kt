package ru.yandex.moykoshelek.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yandex.moykoshelek.di.ViewModelKey
import ru.yandex.moykoshelek.ui.balance.BalanceViewModel
import ru.yandex.moykoshelek.ui.report.ReportViewModel
import ru.yandex.moykoshelek.ui.settings.SettingsViewModel
import ru.yandex.moykoshelek.ui.transaction.TransactionViewModel
import ru.yandex.moykoshelek.ui.wallet.WalletViewModel
import ru.yandex.moykoshelek.util.DaggerViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(BalanceViewModel::class)
    abstract fun bindBalanceViewModel(balanceViewModel: BalanceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    abstract fun bindAddTransactionViewModel(transactionViewModel: TransactionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun bindAddWalletViewModel(walletViewModel: WalletViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportViewModel::class)
    abstract fun bindReportViewModel(reportViewModel: ReportViewModel): ViewModel
}