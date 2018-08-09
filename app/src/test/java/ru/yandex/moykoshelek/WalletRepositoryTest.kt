package ru.yandex.moykoshelek

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.WalletTypes
import ru.yandex.moykoshelek.data.repositories.WalletRepository

@RunWith(JUnit4::class)
class WalletRepositoryTest {

    private lateinit var walletDao: WalletDao
    private lateinit var walletRepository: WalletRepository
    val walletStub = Wallet(1, WalletTypes.BANK_ACCOUNT, "testwallet", 1000.0, CurrencyTypes.RUB, "2", "22/12")
    val walletListStub = listOf(walletStub, walletStub)

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        walletDao = mock(WalletDao::class.java)
        walletRepository = WalletRepository(walletDao)
    }

    @Test
    fun getWallets() {
        runBlocking {
            val expectedWallets = MutableLiveData<List<Wallet>>()
            expectedWallets.value = walletListStub
            `when`(walletDao.getAll()).thenReturn(expectedWallets)

            assertEquals(expectedWallets, walletRepository.getWallets())

            verify(walletDao).getAll()
        }
    }

    @Test
    fun addWallet() {
        runBlocking {
            assertNotNull(walletRepository.addWallet(walletStub))

            verify(walletDao).insert(walletStub)
        }
    }

    @Test
    fun updateWallet() {
        runBlocking {
            assertNotNull(walletRepository.updateWallet(walletStub))

            verify(walletDao).update(walletStub)
        }
    }

    @Test
    fun updateWalletAfterTransaction() {
        runBlocking {
            val walletId = 1
            val transactionCost = 1.5
            assertNotNull(walletRepository.updateWalletAfterTransaction(walletId, transactionCost))

            verify(walletDao).executeTransaction(walletId, transactionCost)
        }
    }

    @Test
    fun deleteWallet() {
        runBlocking {
            assertNotNull(walletRepository.deleteWallet(walletStub))

            verify(walletDao).delete(walletStub)
        }
    }
}