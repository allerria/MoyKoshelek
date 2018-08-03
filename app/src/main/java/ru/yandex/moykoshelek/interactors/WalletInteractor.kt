package ru.yandex.moykoshelek.interactors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import javax.inject.Inject

class WalletInteractor @Inject constructor(
        private val walletRepository: WalletRepository,
        private val transactionsRepository: TransactionsRepository,
        private val currencyRateRepository: CurrencyRateRepository
) {
    fun getWallets() = walletRepository.getWallets()

    fun getTransactions() = transactionsRepository.getTransactions()

    fun getTransactions(walletId: Int) =
            Transformations.map(transactionsRepository.getTransactions())
            { transactions -> transactions.filter { walletId == it.walletId }.sortedByDescending { it.id } }

    fun getCategories() = transactionsRepository.getCategories()

    fun getCurrencyRate() = currencyRateRepository.getCurrencyRate()

    fun addWallet(wallet: Wallet) {
        walletRepository.addWallet(wallet)
    }

    fun addTransaction(transaction: Transaction) {
        transactionsRepository.addTransaction(transaction)
    }

    fun updateWallet(wallet: Wallet) {
        walletRepository.updateWallet(wallet)
    }

    fun setCurrencyRate(currencyRate: Float) {
        currencyRateRepository.addOrUpdateCurrencyRate(currencyRate)
    }

}