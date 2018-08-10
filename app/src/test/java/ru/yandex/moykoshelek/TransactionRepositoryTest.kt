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
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
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
    private val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val transactionsListStub = listOf(transactionStub, transactionStub)
    private val periodTransactionStub = PeriodTransaction(1, getCurrentDateTimeBeforeDays(14), 7, 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val periodTransactionStub1 = PeriodTransaction(2, getCurrentDateTimeBeforeDays(30), 10, 5.0, CurrencyTypes.RUB, TransactionTypes.OUT, 1, "agagaga")
    private val periodTransactionsListStub = listOf(periodTransactionStub, periodTransactionStub1)
    private val templateTransactionStub = TemplateTransaction(1, "template", null, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val templateTransactionStub1 = TemplateTransaction(2, "template", 7, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val templateTransactionsListStub = listOf(templateTransactionStub, templateTransactionStub1)


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
    fun getTransactionById() {
        runBlocking {
            val expectedTransactions = MutableLiveData<Transaction>()
            expectedTransactions.value = transactionStub
            `when`(transactionDao.getById(transactionStub.id)).thenReturn(expectedTransactions)

            assertEquals(expectedTransactions, transactionsRepository.getTransactionById(transactionStub.id))

            verify(transactionDao).getById(transactionStub.id)
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
    fun deleteTransaction() {
        runBlocking {
            assertNotNull(transactionsRepository.deleteTransaction(transactionStub))

            verify(transactionDao).delete(transactionStub)
        }
    }

    @Test
    fun getPeriodTransactions() {
        runBlocking {
            val expectedTransactionsList = MutableLiveData<List<PeriodTransaction>>()
            expectedTransactionsList.value = periodTransactionsListStub
            `when`(periodTransactionDao.getAll()).thenReturn(expectedTransactionsList)

            assertEquals(expectedTransactionsList, transactionsRepository.getPeriodTransactions())

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
    fun addPeriodTransaction() {
        runBlocking {
            assertNotNull(transactionsRepository.addPeriodTransaction(periodTransactionStub))

            verify(periodTransactionDao).insert(periodTransactionStub)
        }
    }

    @Test
    fun deletePeriodTransaction() {
        runBlocking {
            assertNotNull(transactionsRepository.deletePeriodTransaction(periodTransactionStub))

            verify(periodTransactionDao).delete(periodTransactionStub)
        }
    }

    @Test
    fun getTemplateTransactions() {
        runBlocking {
            val expectedTemplateTransactions = MutableLiveData<List<TemplateTransaction>>()
            expectedTemplateTransactions.value = templateTransactionsListStub
            `when`(templateTransactionDao.getAll()).thenReturn(expectedTemplateTransactions)

            assertEquals(expectedTemplateTransactions, transactionsRepository.getTemplateTransactions())
        }
    }

    @Test
    fun addTemplateTransaction() {
        runBlocking {
            assertNotNull(transactionsRepository.addTemplateTransaction(templateTransactionStub))

            verify(templateTransactionDao).insert(templateTransactionStub)
        }
    }

    @Test
    fun deleteTemplateTransaction() {
        runBlocking {
            assertNotNull(transactionsRepository.deleteTemplateTransaction(templateTransactionStub))

            verify(templateTransactionDao).delete(templateTransactionStub)
        }
    }

}