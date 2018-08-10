package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class TransactionViewModel @Inject constructor(private val walletInteractor: WalletInteractor) : ViewModel() {

    val wallets = async { walletInteractor.getWallets() }

    val templateTransactions = async { walletInteractor.getTemplateTransactions() }

    val transactions = async { walletInteractor.getTransactions() }

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
        walletInteractor.addPeriodTransaction(periodTransaction)
        walletInteractor.executeTransaction(transaction)
    }

    fun createTemplateTransaction(transaction: Transaction, name: String, period: Int?) = launch {
        val templateTransaction = TemplateTransaction(0, name, period, transaction.date, transaction.cost,
                transaction.currency, transaction.type, transaction.walletId, transaction.category)
        walletInteractor.addTemplateTransaction(templateTransaction)
    }

    fun getTransactionById(transactionId: Int) = async { walletInteractor.getTransactionById(transactionId) }

    fun getPeriodTransactionById(periodTransactionId: Int) = async { walletInteractor.getPeriodTransaction(periodTransactionId) }

    fun updateTransaction(transaction: Transaction, oldTransaction: Transaction) = launch {
        var oldCost = 0.0
        var newCost = 0.0
        newCost = if (transaction.type == TransactionTypes.IN) transaction.cost else -transaction.cost
        oldCost = if (oldTransaction.type == TransactionTypes.IN) -oldTransaction.cost else oldTransaction.cost

        walletInteractor.updateWalletBalance(oldTransaction.walletId, oldCost)
        walletInteractor.updateWalletBalance(transaction.walletId, newCost)
        walletInteractor.updateTransaction(transaction)
    }

    fun updatePeriodTransaction(periodTransaction: PeriodTransaction, oldPeriodTransaction: PeriodTransaction) = launch {
        var oldCost = 0.0
        var newCost = 0.0
        newCost = if (periodTransaction.type == TransactionTypes.IN) periodTransaction.cost else -periodTransaction.cost
        oldCost = if (oldPeriodTransaction.type == TransactionTypes.IN) -oldPeriodTransaction.cost else oldPeriodTransaction.cost

        walletInteractor.updateWalletBalance(oldPeriodTransaction.walletId, oldCost)
        walletInteractor.updateWalletBalance(periodTransaction.walletId, newCost)
        walletInteractor.updatePeriodTransaction(periodTransaction)
    }

    fun updateTemplateTransaction(templateTransaction: TemplateTransaction, oldTemplateTransaction: TemplateTransaction) = launch {
        var oldCost = 0.0
        var newCost = 0.0
        newCost = if (templateTransaction.type == TransactionTypes.IN) templateTransaction.cost else -templateTransaction.cost
        oldCost = if (oldTemplateTransaction.type == TransactionTypes.IN) -oldTemplateTransaction.cost else oldTemplateTransaction.cost

        walletInteractor.updateWalletBalance(oldTemplateTransaction.walletId, oldCost)
        walletInteractor.updateWalletBalance(templateTransaction.walletId, newCost)
        walletInteractor.updateTemplateTransaction(templateTransaction)
    }

    fun deleteTransaction(transaction: Transaction) = launch {
        var oldCost = 0.0
        oldCost = if (transaction.type == TransactionTypes.IN) -transaction.cost else transaction.cost
        walletInteractor.updateWalletBalance(transaction.walletId, oldCost)

        walletInteractor.deleteTransaction(transaction)
    }

    fun deletePeriodTransaction(periodTransaction: PeriodTransaction) = launch {
        var oldCost = 0.0
        oldCost = if (periodTransaction.type == TransactionTypes.IN) -periodTransaction.cost else periodTransaction.cost
        walletInteractor.updateWalletBalance(periodTransaction.walletId, oldCost)

        walletInteractor.deletePeriodTransaction(periodTransaction)
    }

    fun deleteTemplateTransaction(templateTransaction: TemplateTransaction) = launch {
        var oldCost = 0.0
        oldCost = if (templateTransaction.type == TransactionTypes.IN) -templateTransaction.cost else templateTransaction.cost
        walletInteractor.updateWalletBalance(templateTransaction.walletId, oldCost)

        walletInteractor.deleteTemplateTransaction(templateTransaction)
    }

    fun getWalletByTag(tag: String) = async { wallets.await().value!!.find { "${it.name}-${it.balance.formatMoney(it.currency)}" == tag }!! }

}
