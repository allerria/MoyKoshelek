package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.dao.*
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import javax.inject.Inject

class TransactionsRepository(private val transactionDao: TransactionDao, private val periodTransactionDao: PeriodTransactionDao) {

    fun getTransactions(): LiveData<List<Transaction>> = transactionDao.getAll()

    fun getTransactionsByWalletId(walletId: Int): LiveData<List<Transaction>> = transactionDao.getAllByWalletId(walletId)

    fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    fun addTransactions(transactions: List<Transaction>) {
        transactionDao.insert(transactions)
    }

    fun getPeriodTransactions(): List<PeriodTransaction> = periodTransactionDao.getAll()

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction = periodTransactionDao.getPeriodTransaction(periodTransactionId)

    fun addPeriodTransaction(periodTransaction: PeriodTransaction) {
        periodTransactionDao.insert(periodTransaction)
    }

}