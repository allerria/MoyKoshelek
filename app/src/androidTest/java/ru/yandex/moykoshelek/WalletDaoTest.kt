package ru.yandex.moykoshelek

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import org.junit.Assert.assertNotNull
import ru.yandex.moykoshelek.util.TestUtils.getValueFromLiveData

class WalletDaoTest : DbTest() {
    private lateinit var walletDao: WalletDao
    private val walletStub = Wallet(1, "testwallet", 1000.0, CurrencyTypes.RUB)
    private val walletStub1 = Wallet(2, "testwallet", 1000.0, CurrencyTypes.RUB)
    private val walletStubList = listOf(walletStub, walletStub1)

    @Before
    fun setUp() {
        walletDao = appDatabase.walletDao
    }

    @Test
    fun insertAndGetById() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertEquals(walletStub, getValueFromLiveData(walletDao.getById(walletStub.id)))
        }
    }

    @Test
    fun insertManyAndGetAll() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStubList))
            assertEquals(walletStubList, getValueFromLiveData(walletDao.getAll()))
        }
    }

    @Test
    fun update() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))
            val expectedWallet = walletStub
            expectedWallet.balance = 4.0

            assertNotNull(walletDao.update(expectedWallet))

            assertEquals(expectedWallet, getValueFromLiveData(walletDao.getById(expectedWallet.id)))
        }
    }

    @Test
    fun executeTransaction() {
        runBlocking {
            val transactionCost = 5.3
            val expectedTransaction = walletStub
            assertNotNull(walletDao.insert(expectedTransaction))
            expectedTransaction.balance += transactionCost

            assertNotNull(walletDao.executeTransaction(expectedTransaction.id, transactionCost))

            assertEquals(expectedTransaction, getValueFromLiveData(walletDao.getById(expectedTransaction.id)))
        }
    }

    @Test
    fun delete() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertNotNull(walletDao.delete(walletStub))

            assertEquals(listOf<List<Wallet>>(), getValueFromLiveData(walletDao.getAll()))
        }
    }
}