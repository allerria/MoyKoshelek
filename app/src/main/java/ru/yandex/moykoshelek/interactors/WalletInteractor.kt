package ru.yandex.moykoshelek.interactors

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
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
import ru.yandex.moykoshelek.extensions.toString
import timber.log.Timber
import java.util.*
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
            { transactions -> transactions.filter { walletId == it.walletId }.sortedByDescending { it.date } }

    fun getCurrencyRate() = currencyRateRepository.getCurrencyRate()

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

    fun updateWallet(wallet: Wallet) {
        walletRepository.updateWallet(wallet)
    }

    fun updateCurrencyRate() {
        currencyRateRepository.updateCurrencyRate()
    }

    fun addPeriodTransactionAndGetId(periodTransaction: PeriodTransaction) = transactionsRepository.addPeriodTransactions(periodTransaction)

    fun getPeriodTransaction(periodTransactionId: Int) = transactionsRepository.getPeriodTransaction(periodTransactionId)

    fun getLastPeriodTransactions(): List<Transaction> = transactionsRepository.getLastPeriodTransactions()

    fun executePeriodTransactions() {
        transactionsRepository.getLastPeriodTransactions().forEach {
            if (it.periodTransactionId != null) {
                val periodTransaction = transactionsRepository.getPeriodTransaction(it.periodTransactionId!!)
                val calendarNow = Calendar.getInstance()
                val calendarLast = Calendar.getInstance()
                calendarNow.time = getCurrentDateTime()
                calendarLast.time = it.date
                calendarLast.add(Calendar.DAY_OF_MONTH, periodTransaction.period)
                it.id = 0
                while (calendarLast.before(calendarNow)) {
                    val copy = it.copy()
                    copy.date = calendarLast.time
                    executeTransaction(copy)
                    Timber.d(calendarLast.time.toString())
                    calendarLast.add(Calendar.DAY_OF_MONTH, periodTransaction.period)
                }
            }
        }
    }
}