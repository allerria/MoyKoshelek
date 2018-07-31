package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import ru.yandex.moykoshelek.data.datasource.database.dao.TransactionDataDao
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import javax.inject.Inject

class TransactionsRepository @Inject constructor(private val transactionDataDao: TransactionDataDao) {

    fun getTransactions(): LiveData<List<TransactionData>> = transactionDataDao.getAll()

    fun getCategories(): LiveData<List<String>> = transactionDataDao.getCategories()

    fun addTransaction(transactionData: TransactionData) {
        transactionDataDao.insert(transactionData)
    }

}