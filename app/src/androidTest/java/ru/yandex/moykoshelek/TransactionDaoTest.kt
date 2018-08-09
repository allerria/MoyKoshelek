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
import ru.yandex.moykoshelek.data.entities.WalletTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.util.TestUtils.getValue

class TransactionDaoTest: DbTest() {

    private lateinit var transactionDao: TransactionDao
    private val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    private val transactionStub1 = Transaction(2, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    private val transactionsListStub = listOf(transactionStub, transactionStub1)
    private lateinit var walletDao: WalletDao
    private val walletStub = Wallet(1, WalletTypes.BANK_ACCOUNT, "testwallet", 1000.0, CurrencyTypes.RUB, "2", "22/12")

    @Before
    fun setUp() {
        transactionDao = appDatabase.transactionDao
        walletDao = appDatabase.walletDao
    }

    @Test
    fun insertOrUpdateManyAndGetAll() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertNotNull(transactionDao.insertOrUpdate(transactionsListStub))

            assertEquals(transactionsListStub, getValue(transactionDao.getAll()))
        }
    }

    @Test
    fun insertOrUpdateAndGetByWalletId() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))

            assertNotNull(transactionDao.insertOrUpdate(transactionStub))

            assertEquals(listOf(transactionStub), getValue(transactionDao.getAllByWalletId(transactionStub.walletId)))
        }
    }

    @Test
    fun delete() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))
            assertNotNull(transactionDao.insertOrUpdate(transactionStub))

            assertNotNull(transactionDao.delete(transactionStub))

            assertEquals(listOf<List<Transaction>>(), getValue(transactionDao.getAll()))
        }
    }

    @Test
    fun getById() {
        runBlocking {
            assertNotNull(walletDao.insert(walletStub))
            assertNotNull(transactionDao.insertOrUpdate(transactionStub))

            assertEquals(transactionStub, getValue(transactionDao.getById(transactionStub.id)))
        }
    }

}