package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.interactors.WalletInteractor
import java.util.*
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
        val periodTransactionId = walletInteractor.addPeriodTransactionAndGetId(periodTransaction)
        transaction.periodTransactionId = periodTransactionId.toInt()
        walletInteractor.executeTransaction(transaction)
    }

    fun getWallet(position: Int) = wallets.value!![position]

}