package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class BalanceViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel(){

    val wallets: LiveData<List<WalletData>> = walletInteractor.getWallets()
    val transactions: LiveData<List<TransactionData>> = walletInteractor.getTransactions()
    val currencyRate: LiveData<Float> = walletInteractor.getCurrencyRate()
}