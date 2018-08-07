package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class AddTransactionViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel() {

    val wallets: LiveData<List<Wallet>> = walletInteractor.getWallets()

    fun executeTransaction(transaction: Transaction) {
        walletInteractor.executeTransaction(transaction)
    }

    fun executePeriodTransaction(transaction: Transaction, period: Int) {
        val periodTransaction = PeriodTransaction()
        periodTransaction.time = transaction.date
        periodTransaction.period = period
        periodTransaction.walletId = transaction.walletId
        periodTransaction.category = transaction.category
        periodTransaction.cost = transaction.cost
        periodTransaction.currency = transaction.currency
        periodTransaction.type = transaction.type
        periodTransaction.placeholder = transaction.placeholder
        walletInteractor.addPeriodTransaction(periodTransaction)
        walletInteractor.executeTransaction(transaction)
    }

    fun getWallet(position: Int) = wallets.value!![position]

}