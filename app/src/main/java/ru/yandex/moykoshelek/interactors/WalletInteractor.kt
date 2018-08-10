package ru.yandex.moykoshelek.interactors

import android.arch.lifecycle.LiveData
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.util.TestUtils.getValueFromLiveData
import java.util.*
import javax.inject.Inject

class WalletInteractor @Inject constructor(
        private val walletRepository: WalletRepository,
        private val transactionsRepository: TransactionsRepository,
        private val currencyRateRepository: CurrencyRateRepository
) {

    fun getWallets(): LiveData<List<Wallet>> = walletRepository.getWallets()

    fun getWallet(walletId: Int): LiveData<Wallet> = walletRepository.getWalletById(walletId)

    fun addWallet(wallet: Wallet) {
        walletRepository.addWallet(wallet)
    }

    fun updateWallet(wallet: Wallet) {
        walletRepository.updateWallet(wallet)
    }

    fun updateWalletBalance(walletId: Int, transactionCost: Double) {
        walletRepository.updateWalletAfterTransaction(walletId, transactionCost)
    }

    fun deleteWallet(wallet: Wallet) {
        walletRepository.deleteWallet(wallet)
    }

    fun getTransactions(): LiveData<List<Transaction>> = transactionsRepository.getTransactions()

    fun getTransactions(walletId: Int): LiveData<List<Transaction>> = transactionsRepository.getTransactionsByWalletId(walletId)

    fun getTransactionById(transactionId: Int): LiveData<Transaction> = transactionsRepository.getTransactionById(transactionId)

    fun addTransaction(transaction: Transaction) {
        transactionsRepository.addTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) {
        transactionsRepository.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        transactionsRepository.deleteTransaction(transaction)
    }

    fun executeTransaction(transaction: Transaction) {
        transactionsRepository.addTransaction(transaction)
        var transactionCost = transaction.cost
        if (transaction.type == TransactionTypes.OUT) {
            transactionCost *= -1
        }
        walletRepository.updateWalletAfterTransaction(transaction.walletId, transactionCost)
    }

    fun executeTransactions(transactions: List<Transaction>) {
        if (transactions.isNotEmpty()) {
            transactionsRepository.addTransactions(transactions)
            walletRepository.updateWalletAfterTransaction(transactions.first().walletId, transactionsSum(transactions))
        }
    }

    fun getDeferredTransactions(periodTransactions: List<PeriodTransaction>): List<Transaction> {
        val toExecuteTransactions = mutableListOf<Transaction>()
        periodTransactions.forEach {
            val calendarNow = Calendar.getInstance()
            val calendarLast = Calendar.getInstance()
            calendarNow.time = getCurrentDateTime()
            calendarLast.time = it.time
            calendarLast.add(Calendar.DAY_OF_MONTH, it.period)
            while (calendarLast.before(calendarNow)) {
                val transaction = Transaction(0, calendarLast.time, it.cost, it.currency, it.type, it.walletId, it.category)
                toExecuteTransactions.add(transaction)
                calendarLast.add(Calendar.DAY_OF_MONTH, it.period)
            }
        }
        return toExecuteTransactions
    }

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction = transactionsRepository.getPeriodTransaction(periodTransactionId)

    fun getPeriodTransactions(): LiveData<List<PeriodTransaction>> = transactionsRepository.getPeriodTransactions()

    fun addPeriodTransaction(periodTransaction: PeriodTransaction) = transactionsRepository.addPeriodTransaction(periodTransaction)

    fun executePeriodTransactions() {
        val periodTransactions = transactionsRepository.getPeriodTransactions()
        val deferredTransactions = getDeferredTransactions(getValueFromLiveData(periodTransactions))
        executeTransactions(deferredTransactions)
    }

    fun updatePeriodTransaction(periodTransaction: PeriodTransaction) {
        transactionsRepository.deletePeriodTransaction(periodTransaction)
    }

    fun deletePeriodTransaction(periodTransaction: PeriodTransaction) {
        transactionsRepository.deletePeriodTransaction(periodTransaction)
    }

    fun getTemplateTransactions(): LiveData<List<TemplateTransaction>> = transactionsRepository.getTemplateTransactions()

    fun addTemplateTransaction(templateTransaction: TemplateTransaction) {
        transactionsRepository.addTemplateTransaction(templateTransaction)
    }

    fun updateTemplateTransaction(templateTransaction: TemplateTransaction) {
        transactionsRepository.updateTemplateTransaction(templateTransaction)
    }

    fun deleteTemplateTransaction(templateTransaction: TemplateTransaction) {
        transactionsRepository.deleteTemplateTransaction(templateTransaction)
    }

    fun getCurrencyRate(): LiveData<Float> = currencyRateRepository.getCurrencyRate()

    fun updateCurrencyRate() {
        currencyRateRepository.updateCurrencyRate()
    }

    fun transactionsSum(transactions: List<Transaction>): Double {
        var sum = 0.0
        transactions.forEach {
            sum += if (it.type == TransactionTypes.IN) it.cost else -it.cost
        }
        return sum
    }

}