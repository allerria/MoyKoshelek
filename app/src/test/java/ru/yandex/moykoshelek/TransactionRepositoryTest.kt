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
import ru.yandex.moykoshelek.data.datasource.local.dao.PeriodTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TemplateTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.dao.TransactionDao
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.getCurrentDateTimeBeforeDays

@RunWith(JUnit4::class)
class TransactionRepositoryTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var periodTransactionDao: PeriodTransactionDao
    private lateinit var templateTransactionDao: TemplateTransactionDao
    private lateinit var transactionsRepository: TransactionsRepository
    val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    val transactionsListStub = listOf(transactionStub, transactionStub)
    val periodTransactionStub = PeriodTransaction(1, getCurrentDateTimeBeforeDays(14), 7, 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    val periodTransactionStub1 = PeriodTransaction(2, getCurrentDateTimeBeforeDays(30), 10, 5.0, CurrencyTypes.RUB, "asd", TransactionTypes.OUT, 1, "agagaga")
    val periodTransactionsListStub = listOf(periodTransactionStub, periodTransactionStub1)

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        transactionDao = mock(TransactionDao::class.java)
        periodTransactionDao = mock(PeriodTransactionDao::class.java)
        templateTransactionDao = mock(TemplateTransactionDao::class.java)
        transactionsRepository = TransactionsRepository(transactionDao, periodTransactionDao, templateTransactionDao)
    }

    @Test
    fun getTransactions() {
        runBlocking {
            val expectedTransactions = MutableLiveData<List<Transaction>>()
            expectedTransactions.value = transactionsListStub
            `when`(transactionDao.getAll()).thenReturn(expectedTransactions)

            assertEquals(expectedTransactions, transactionsRepository.getTransactions())

            verify(transactionDao).getAll()
        }
    }

    @Test
    fun getTransactionsByWalletId() {
        runBlocking {
            val expectedTransactions = MutableLiveData<List<Transaction>>()
            val walletId = 1
            expectedTransactions.value = transactionsListStub
            `when`(transactionDao.getAllByWalletId(walletId)).thenReturn(expectedTransactions)

            assertEquals(expectedTransactions, transactionsRepository.getTransactionsByWalletId(walletId))

            verify(transactionDao).getAllByWalletId(walletId)
        }
    }

    @Test
    fun addTransaction() {
        assertNotNull(transactionsRepository.addTransaction(transactionStub))

        verify(transactionDao).insert(transactionStub)
    }

    @Test
    fun addTransactions() {
        runBlocking {
            assertNotNull(transactionsRepository.addTransactions(transactionsListStub))

            verify(transactionDao).insert(transactionsListStub)
        }
    }

    @Test
    fun getPeriodTransactions() {
        runBlocking {
            `when`(periodTransactionDao.getAll()).thenReturn(periodTransactionsListStub)

            assertEquals(periodTransactionsListStub, transactionsRepository.getPeriodTransactions())

            verify(periodTransactionDao).getAll()
        }
    }

    @Test
    fun getPeriodTransactionById() {
        runBlocking {
            val periodTransactionId = 1
            `when`(periodTransactionDao.getById(periodTransactionId)).thenReturn(periodTransactionStub)

            assertEquals(periodTransactionStub, transactionsRepository.getPeriodTransaction(periodTransactionId))

            verify(periodTransactionDao).getById(periodTransactionId)
        }
    }

    @Test
    fun addPeriodTransaction(){
        runBlocking {
            assertNotNull(transactionsRepository.addPeriodTransaction(periodTransactionStub))

            verify(periodTransactionDao).insert(periodTransactionStub)
        }
    }

}