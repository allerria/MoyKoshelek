package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class AddWalletViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {

    fun addWallet(wallet: Wallet) {
        walletInteractor.addWallet(wallet)
    }

}