package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.getWallets
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import javax.inject.Inject

class TransactionsRepository @Inject constructor(private val transactionDao: TransactionDao) {

    fun getTransactions(): LiveData<List<Transaction>> {
        lateinit var result: LiveData<List<Transaction>>
        runBlocking {
            result = transactionDao.getWallets()
        }
        return result
    }

    fun getCategories(): LiveData<List<String>> = transactionDao.getCategories()

    fun addTransaction(transaction: Transaction) {

        launch {
            transactionDao.insert(transaction)
        }

    }

}