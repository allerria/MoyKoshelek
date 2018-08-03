package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.widget.Toast
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import timber.log.Timber
import javax.inject.Inject

class BalanceViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel(){

    val wallets: LiveData<List<Wallet>> = walletInteractor.getWallets()

    fun getTransactions(walletId: Int) = walletInteractor.getTransactions(walletId)

    fun getWalletId(position: Int): Int {
        return wallets.value!![position].id!!
    }
}