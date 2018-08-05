package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.dao.*
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import javax.inject.Inject

class TransactionsRepository @Inject constructor(private val transactionDao: TransactionDao, private val periodTransactionDao: PeriodTransactionDao) {

    fun getTransactions(): LiveData<List<Transaction>> {
        lateinit var result: LiveData<List<Transaction>>
        runBlocking {
            result = transactionDao.getTransactions()
        }
        return result
    }

    fun addTransaction(transaction: Transaction) {
        launch {
            transactionDao.insert(transaction)
        }
    }

    fun getPeriodTransactions(): List<PeriodTransaction> {
        lateinit var result: List<PeriodTransaction>
        runBlocking {
            result = periodTransactionDao.getPeriodTransactions()
        }
        return result
    }

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction {
        lateinit var result: PeriodTransaction
        runBlocking {
            result = periodTransactionDao.getTransaction(periodTransactionId)
        }
        return result
    }

    fun getLastPeriodTransactions(): List<Transaction> {
        lateinit var result: List<Transaction>
        runBlocking {
            result = transactionDao.getLastPeriodTransactions()
        }
        return result
    }

    fun addPeriodTransactions(periodTransaction: PeriodTransaction): Long {
        var result = 0L
        runBlocking {
            result = periodTransactionDao.insertAndGetId(periodTransaction)
        }
        return result
    }
}