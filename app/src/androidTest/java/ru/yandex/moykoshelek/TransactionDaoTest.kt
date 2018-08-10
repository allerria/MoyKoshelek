package ru.yandex.moykoshelek

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.WalletDao
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.util.TestUtils.getValueFromLiveData

class TransactionDaoTest : DbTest() {

    private lateinit var transactionDao: TransactionDao
    private val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val transactionStub1 = Transaction(2, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val transactionsListStub = listOf(transactionStub, transactionStub1)
    private lateinit var walletDao: WalletDao
    private val walletStub = Wallet(1, "testwallet", 1000.0, CurrencyTypes.RUB)

    @Before
    fun setUp() {
        transactionDao = appDatabase.transactionDao
        walletDao = appDatabase.walletDao
    }

    @Test
    fun insertManyAndGetAll() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertNotNull(transactionDao.insert(transactionsListStub))

            assertEquals(transactionsListStub, getValueFromLiveData(transactionDao.getAll()))
        }
    }

    @Test
    fun insertAndGetByWalletId() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertNotNull(transactionDao.insert(transactionStub))

            assertEquals(listOf(transactionStub), getValueFromLiveData(transactionDao.getAllByWalletId(transactionStub.walletId)))
        }
    }

    @Test
    fun delete() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))
            assertNotNull(transactionDao.insert(transactionStub))

            assertNotNull(transactionDao.delete(transactionStub))

            assertEquals(listOf<List<Transaction>>(), getValueFromLiveData(transactionDao.getAll()))
        }
    }

    @Test
    fun getById() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))
            assertNotNull(transactionDao.insert(transactionStub))

            assertEquals(transactionStub, getValueFromLiveData(transactionDao.getById(transactionStub.id)))
        }
    }

}