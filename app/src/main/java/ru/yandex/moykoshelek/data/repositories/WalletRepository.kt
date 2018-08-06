package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.dao.getTransactions
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import javax.inject.Inject

class WalletRepository @Inject constructor(private val walletDao: WalletDao) {

    fun getWallets(): LiveData<List<Wallet>> {
        lateinit var result: LiveData<List<Wallet>>
        runBlocking {
            result = walletDao.getTransactions()
        }
        return result
    }

    fun addWallet(wallet: Wallet) {
        launch {
            walletDao.insert(wallet)
        }
    }

    fun updateWallet(wallet: Wallet) {
        launch {
            walletDao.update(wallet)
        }
    }

    fun updateWalletAfterTransaction(walletId: Int, transactionCost: Double) {
        launch {
            walletDao.executeTransaction(walletId, transactionCost)
        }
    }
}