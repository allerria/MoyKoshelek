package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class AddTransactionViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {

    val wallets: LiveData<List<Wallet>> = walletInteractor.getWallets()
    val currencyRate: LiveData<Float> = walletInteractor.getCurrencyRate()
    val categories: LiveData<List<String>> = walletInteractor.getCategories()

    fun addTransaction(transaction: Transaction) {
        walletInteractor.addTransaction(transaction)
    }

    fun updateWallet(wallet: Wallet) {
        walletInteractor.updateWallet(wallet)
    }

}