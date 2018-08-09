package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet

class WalletRepository (private val walletDao: WalletDao) {

    fun getWallets(): LiveData<List<Wallet>> = walletDao.getAll()

    fun addWallet(wallet: Wallet) {
        walletDao.insert(wallet)
    }

    fun updateWallet(wallet: Wallet) {
        walletDao.update(wallet)
    }

    fun updateWalletAfterTransaction(walletId: Int, transactionCost: Double) {
        walletDao.executeTransaction(walletId, transactionCost)
    }

    fun deleteWallet(wallet: Wallet) {
        walletDao.delete(wallet)
    }
}