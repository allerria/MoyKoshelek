package ru.yandex.moykoshelek.interactors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
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

    fun getWallets(): LiveData<List<Wallet>> {
        lateinit var result: LiveData<List<Wallet>>
        runBlocking {
            result = walletRepository.getWallets()
        }
        return result
    }

    fun getTransactions(): LiveData<List<Transaction>> {
        lateinit var result: LiveData<List<Transaction>>
        runBlocking {
            result = transactionsRepository.getTransactions()
        }
        return result
    }

    fun getTransactions(walletId: Int): LiveData<List<Transaction>> {
        lateinit var result: LiveData<List<Transaction>>
        runBlocking {
            result = transactionsRepository.getTransactionsByWalletId(walletId)
        }
        return result
    }

    fun getCurrencyRate(): LiveData<Float> {
        lateinit var result: LiveData<Float>
        runBlocking {
            result = currencyRateRepository.getCurrencyRate()
        }
        return result
    }

    fun addWallet(wallet: Wallet) {
        launch {
            walletRepository.addWallet(wallet)
        }
    }

    fun addTransaction(transaction: Transaction) {
        launch {
            transactionsRepository.addTransaction(transaction)
        }
    }

    fun executeTransaction(transaction: Transaction) {
        launch {
            transactionsRepository.addTransaction(transaction)
            var transactionCost = transaction.cost
            if (transaction.type == TransactionTypes.OUT) {
                transactionCost *= -1
            }
            walletRepository.updateWalletAfterTransaction(transaction.walletId, transactionCost)
        }
    }

    fun executeTransactions(transactions: List<Transaction>) {
        launch {
            if (transactions.isNotEmpty()) {
                transactionsRepository.addTransactions(transactions)
                walletRepository.updateWalletAfterTransaction(transactions.first().walletId, transactionsSum(transactions))
            }
        }
    }

    fun transactionsSum(transactions: List<Transaction>): Double {
        var sum = 0.0
        runBlocking {
            transactions.forEach {
                sum += if (it.type == TransactionTypes.IN) it.cost else -it.cost
            }
        }
        return sum
    }

    fun updateWallet(wallet: Wallet) {
        launch {
            walletRepository.updateWallet(wallet)
        }
    }

    fun updateCurrencyRate() {
        launch {
            currencyRateRepository.updateCurrencyRate()
        }
    }

    fun addPeriodTransaction(periodTransaction: PeriodTransaction) {
        launch {
            transactionsRepository.addPeriodTransaction(periodTransaction)
        }
    }

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction {
        lateinit var result: PeriodTransaction
        runBlocking {
            result = transactionsRepository.getPeriodTransaction(periodTransactionId)
        }
        return result
    }

    fun getDeferredTransactions(periodTransactions: List<PeriodTransaction>): List<Transaction> {
        val toExecuteTransactions = mutableListOf<Transaction>()
        runBlocking {
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
        }
        return toExecuteTransactions
    }

    fun executePeriodTransactions() {
        launch {
            val periodTransactions = transactionsRepository.getPeriodTransactions()
            val deferredTransactions = getDeferredTransactions(periodTransactions)
            executeTransactions(deferredTransactions)
        }
    }
}