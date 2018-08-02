package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class AddTransactionViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {

    val wallets: LiveData<List<WalletData>> = walletInteractor.getWallets()
    val currencyRate: LiveData<Float> = walletInteractor.getCurrencyRate()
    val categories: LiveData<List<String>> = walletInteractor.getCategories()

    fun addTransaction(transaction: TransactionData) {
        walletInteractor.addTransaction(transaction)
    }

    fun updateWallet(wallet: WalletData) {
        walletInteractor.updateWallet(wallet)
    }

}