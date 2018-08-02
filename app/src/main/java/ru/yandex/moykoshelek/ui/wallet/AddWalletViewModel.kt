package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class AddWalletViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {

    fun addWallet(wallet: WalletData) {
        walletInteractor.addWallet(wallet)
    }

}