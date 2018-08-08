package ru.yandex.moykoshelek.interactors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import java.util.*
import javax.inject.Inject

class WalletInteractor @Inject constructor(
        private val walletRepository: WalletRepository,
        private val transactionsRepository: TransactionsRepository,
        private val currencyRateRepository: CurrencyRateRepository
) {

    fun getWallets(): LiveData<List<Wallet>> = walletRepository.getWallets()

    fun getTransactions(): LiveData<List<Transaction>> = transactionsRepository.getTransactions()

    fun getTransactions(walletId: Int): LiveData<List<Transaction>> = transactionsRepository.getTransactionsByWalletId(walletId)

    fun getCurrencyRate(): LiveData<Float> = currencyRateRepository.getCurrencyRate()

    fun addWallet(wallet: Wallet) {
        walletRepository.addWallet(wallet)
    }

    fun addTransaction(transaction: Transaction) {
        transactionsRepository.addTransaction(transaction)
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

    fun transactionsSum(transactions: List<Transaction>): Double {
        var sum = 0.0
        transactions.forEach {
            sum += if (it.type == TransactionTypes.IN) it.cost else -it.cost
        }
        return sum
    }

    fun updateWallet(wallet: Wallet) {
        walletRepository.updateWallet(wallet)
    }

    fun updateCurrencyRate() {
        currencyRateRepository.updateCurrencyRate()
    }

    fun addPeriodTransaction(periodTransaction: PeriodTransaction) = transactionsRepository.addPeriodTransaction(periodTransaction)

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction = transactionsRepository.getPeriodTransaction(periodTransactionId)

    fun getDeferredTransactions(periodTransactions: List<PeriodTransaction>): List<Transaction> {
        val toExecuteTransactions = mutableListOf<Transaction>()
        periodTransactions.forEach {
            val calendarNow = Calendar.getInstance()
            val calendarLast = Calendar.getInstance()
            calendarNow.time = getCurrentDateTime()
            calendarLast.time = it.time
            calendarLast.add(Calendar.DAY_OF_MONTH, it.period)
            while (calendarLast.before(calendarNow)) {
                val transaction = Transaction(0, calendarLast.time, it.cost, it.currency, it.placeholder, it.type, it.walletId, it.category)
                toExecuteTransactions.add(transaction)
                calendarLast.add(Calendar.DAY_OF_MONTH, it.period)
            }
        }
        return toExecuteTransactions
    }

    fun executePeriodTransactions() {
        val periodTransactions = transactionsRepository.getPeriodTransactions()
        val deferredTransactions = getDeferredTransactions(periodTransactions)
        executeTransactions(deferredTransactions)
    }

    fun getTemplateTransactions(): LiveData<List<TemplateTransaction>> = transactionsRepository.getTemplateTransactions()

    fun addTemplateTransaction(templateTransaction: TemplateTransaction) {
        transactionsRepository.addTemplateTransaction(templateTransaction)
    }
}