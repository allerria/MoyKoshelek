package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class WalletViewModel @Inject constructor(private val walletInteractor: WalletInteractor) : ViewModel() {

    val wallets = async { walletInteractor.getWallets() }

    fun getWallet(walletId: Int) = async { walletInteractor.getWallet(walletId) }

    fun addWallet(wallet: Wallet) = launch {
        walletInteractor.addWallet(wallet)
    }

    fun updateWallet(wallet: Wallet) = launch {
        walletInteractor.updateWallet(wallet)
    }

    fun deleteWallet(wallet: Wallet) = launch {
        walletInteractor.deleteWallet(wallet)
    }

}