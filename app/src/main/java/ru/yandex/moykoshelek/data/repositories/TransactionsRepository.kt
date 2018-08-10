package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import ru.yandex.moykoshelek.data.datasource.local.dao.*
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import java.util.*

class TransactionsRepository(
        private val transactionDao: TransactionDao,
        private val periodTransactionDao: PeriodTransactionDao,
        private val templateTransactionDao: TemplateTransactionDao
) {

    fun getTransactions(): LiveData<List<Transaction>> = transactionDao.getAll()

    fun getTransactions(from: Date, to: Date): LiveData<List<Transaction>> = transactionDao.getAll(from, to)

    fun getTransactionsByWalletId(walletId: Int): LiveData<List<Transaction>> = transactionDao.getAllByWalletId(walletId)

    fun getTransactionById(transactionId: Int): LiveData<Transaction> = transactionDao.getById(transactionId)

    fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    fun addTransactions(transactions: List<Transaction>) {
        transactionDao.insert(transactions)
    }

    fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    fun getPeriodTransactions(): LiveData<List<PeriodTransaction>> = periodTransactionDao.getAll()

    fun getPeriodTransaction(periodTransactionId: Int): PeriodTransaction = periodTransactionDao.getById(periodTransactionId)

    fun addPeriodTransaction(periodTransaction: PeriodTransaction) {
        periodTransactionDao.insert(periodTransaction)
    }

    fun updatePeriodTransaction(periodTransaction: PeriodTransaction) {
        periodTransactionDao.update(periodTransaction)
    }

    fun deletePeriodTransaction(periodTransaction: PeriodTransaction) {
        periodTransactionDao.delete(periodTransaction)
    }

    fun getTemplateTransactions(): LiveData<List<TemplateTransaction>> = templateTransactionDao.getAll()

    fun updateTemplateTransaction(templateTransaction: TemplateTransaction) {
        templateTransactionDao.delete(templateTransaction)
    }

    fun addTemplateTransaction(templateTransaction: TemplateTransaction) {
        templateTransactionDao.insert(templateTransaction)
    }

    fun deleteTemplateTransaction(templateTransaction: TemplateTransaction) {
        templateTransactionDao.delete(templateTransaction)
    }
}