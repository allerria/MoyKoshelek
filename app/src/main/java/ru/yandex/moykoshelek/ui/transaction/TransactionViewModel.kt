package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val walletInteractor: WalletInteractor) : ViewModel() {

    val wallets = async { walletInteractor.getWallets() }

    val templateTransactions = async { walletInteractor.getTemplateTransactions() }

    fun executeTransaction(transaction: Transaction) = launch {
        walletInteractor.executeTransaction(transaction)
    }

    fun executePeriodTransaction(transaction: Transaction, period: Int) = launch {
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

    fun createTemplateTransaction(transaction: Transaction, name: String, period: Int?) = launch {
        val templateTransaction = TemplateTransaction(0, name, period, transaction.date, transaction.cost,
                transaction.currency, transaction.placeholder, transaction.type, transaction.walletId, transaction.category)
        walletInteractor.addTemplateTransaction(templateTransaction)
    }

    fun getTransactionById(transactionId: Int) = async { walletInteractor.getTransactionById(transactionId) }

    fun executeAndUpdateTransaction(transaction: Transaction, oldTransaction: Transaction) = launch {
        var oldCost = 0.0
        var newCost = 0.0
        newCost = if (transaction.type == TransactionTypes.IN) transaction.cost else -transaction.cost
        oldCost = if (oldTransaction.type == TransactionTypes.IN) -oldTransaction.cost else oldTransaction.cost

        walletInteractor.updateWalletBalance(oldTransaction.walletId, oldCost)
        walletInteractor.updateWalletBalance(transaction.walletId, newCost)
        walletInteractor.updateTransaction(transaction)
    }
}
