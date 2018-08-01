package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.database.dao.WalletDataDao
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData
import javax.inject.Inject

class WalletRepository @Inject constructor(private val walletDataDao: WalletDataDao) {

    fun getWallets(): LiveData<List<WalletData>> = walletDataDao.getAll()

    fun addWallet(wallet: WalletData) {
        launch {
            walletDataDao.insert(wallet)
        }
    }

    fun updateWallet(wallet: WalletData) {
        launch {
            walletDataDao.update(wallet)
        }
    }

}