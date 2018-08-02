package ru.yandex.moykoshelek.interactors

import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData
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

    fun getCategories() = transactionsRepository.getCategories()

    fun getCurrencyRate() = currencyRateRepository.getCurrencyRate()

    fun addWallet(walletData: WalletData) {
        walletRepository.addWallet(walletData)
    }

    fun addTransaction(transactionData: TransactionData) {
        transactionsRepository.addTransaction(transactionData)
    }

    fun updateWallet(walletData: WalletData) {
        walletRepository.updateWallet(walletData)
    }

    fun setCurrencyRate(currencyRate: Float) {
        currencyRateRepository.addOrUpdateCurrencyRate(currencyRate)
    }
}