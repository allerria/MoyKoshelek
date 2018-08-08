package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.*
import android.widget.Toast
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import timber.log.Timber
import javax.inject.Inject

class BalanceViewModel @Inject constructor(private val walletInteractor: WalletInteractor) : ViewModel() {

    val wallets = async { walletInteractor.getWallets() }

    fun getTransactions(walletId: Int) = async { walletInteractor.getTransactions(walletId) }

    fun updateCurrencyRate() = launch {
        walletInteractor.updateCurrencyRate()
    }

    fun checkPeriodTransactions() = launch {
        walletInteractor.executePeriodTransactions()
    }

}