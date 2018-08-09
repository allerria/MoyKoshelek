package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class WalletViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {
    fun addWallet(wallet: Wallet) = launch {
        walletInteractor.addWallet(wallet)
    }
}