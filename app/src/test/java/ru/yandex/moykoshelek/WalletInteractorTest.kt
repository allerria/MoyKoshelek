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
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.getCurrentDateTimeBeforeDays
import ru.yandex.moykoshelek.interactors.WalletInteractor
import java.util.*

@RunWith(JUnit4::class)
class WalletInteractorTest {

    private lateinit var currencyRateRepository: CurrencyRateRepository
    private lateinit var transactionsRepository: TransactionsRepository
    private lateinit var walletRepository: WalletRepository
    private lateinit var walletInteractor: WalletInteractor

    private val walletStub = Wallet(1, "testwallet", 1000.0, CurrencyTypes.RUB)
    private val walletListStub = listOf(walletStub, walletStub)
    private val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val transactionsListStub = listOf(transactionStub, transactionStub)
    private val currencyRateStub = 63.0f
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
        transactionsRepository = mock(TransactionsRepository::class.java)
        currencyRateRepository = mock(CurrencyRateRepository::class.java)
        walletRepository = mock(WalletRepository::class.java)
        walletInteractor = WalletInteractor(walletRepository, transactionsRepository, currencyRateRepository)
    }

    @Test
    fun getWallets() {
        runBlocking {
            val expectedWallets = MutableLiveData<List<Wallet>>()
            expectedWallets.value = walletListStub
            `when`(walletRepository.getWallets()).thenReturn(expectedWallets)

            assertEquals(expectedWallets, walletInteractor.getWallets())

            verify(walletRepository).getWallets()
        }
    }

    @Test
    fun addWallet() {
        runBlocking {
            assertNotNull(walletInteractor.addWallet(walletStub))

            verify(walletRepository).addWallet(walletStub)
        }
    }

    @Test
    fun updateWallet() {
        runBlocking {
            assertNotNull(walletInteractor.updateWallet(walletStub))

            verify(walletRepository).updateWallet(walletStub)
        }
    }

    @Test
    fun deleteWallet() {
        runBlocking {
            assertNotNull(walletInteractor.deleteWallet(walletStub))

            verify(walletRepository).deleteWallet(walletStub)
        }
    }

    @Test
    fun getTransactions() {
        runBlocking {
            val expectedTransactions = MutableLiveData<List<Transaction>>()
            expectedTransactions.value = transactionsListStub
            `when`(transactionsRepository.getTransactions()).thenReturn(expectedTransactions)

            assertEquals(expectedTransactions, walletInteractor.getTransactions())

            verify(transactionsRepository).getTransactions()
        }
    }

    @Test
    fun getTransactionsByWalletId() {
        runBlocking {
            val walletId = 1
            val expectedTransaction = MutableLiveData<List<Transaction>>()
            expectedTransaction.value = transactionsListStub
            `when`(transactionsRepository.getTransactionsByWalletId(walletId)).thenReturn(expectedTransaction)

            assertEquals(expectedTransaction, walletInteractor.getTransactions(walletId))

            verify(transactionsRepository).getTransactionsByWalletId(walletId)
        }
    }

    @Test
    fun getTransactionById() {
        runBlocking {
            val expectedTransaction = MutableLiveData<Transaction>()
            expectedTransaction.value = transactionStub
            `when`(transactionsRepository.getTransactionById(transactionStub.id)).thenReturn(expectedTransaction)

            assertEquals(expectedTransaction, walletInteractor.getTransactionById(transactionStub.id))

            verify(transactionsRepository).getTransactionById(transactionStub.id)
        }
    }

    @Test
    fun addTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.addTransaction(transactionStub))

            verify(transactionsRepository).addTransaction(transactionStub)
        }
    }

    @Test
    fun deleteTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.deleteTransaction(transactionStub))

            verify(transactionsRepository).deleteTransaction(transactionStub)
        }
    }

    @Test
    fun executeTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.executeTransaction(transactionStub))

            verify(transactionsRepository).addTransaction(transactionStub)
            verify(walletRepository).updateWalletAfterTransaction(transactionStub.walletId, if (transactionStub.type == TransactionTypes.IN) transactionStub.cost else -transactionStub.cost)
        }
    }

    @Test
    fun executeTransactions() {
        runBlocking {
            assertNotNull(walletInteractor.executeTransactions(transactionsListStub))

            verify(transactionsRepository).addTransactions(transactionsListStub)
            verify(walletRepository).updateWalletAfterTransaction(transactionsListStub.first().id, walletInteractor.transactionsSum(transactionsListStub))
        }
    }

    @Test
    fun getDeferredTransactions() {
        runBlocking {
            val periodTransactions = listOf(periodTransactionStub)
            val cal = Calendar.getInstance()
            cal.time = periodTransactionStub.time
            cal.add(Calendar.DAY_OF_MONTH, periodTransactionStub.period)
            val time1 = cal.time
            cal.add(Calendar.DAY_OF_MONTH, periodTransactionStub.period)
            val time2 = cal.time
            val expectedTransactions = listOf(Transaction(0, time1, 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto"), Transaction(0, time2, 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto"))

            assertEquals(expectedTransactions, walletInteractor.getDeferredTransactions(periodTransactions))
        }
    }

    @Test
    fun getPeriodTransaction() {
        runBlocking {
            val periodTransactionId = 1
            val expectedPeriodTransaction = periodTransactionStub
            `when`(transactionsRepository.getPeriodTransaction(periodTransactionId)).thenReturn(periodTransactionStub)

            assertEquals(expectedPeriodTransaction, walletInteractor.getPeriodTransaction(periodTransactionId))

            verify(transactionsRepository).getPeriodTransaction(periodTransactionId)
        }
    }

    @Test
    fun addPeriodTransactionAndGetId() {
        runBlocking {
            assertNotNull(walletInteractor.addPeriodTransaction(periodTransactionStub))

            verify(transactionsRepository).addPeriodTransaction(periodTransactionStub)
        }
    }

    @Test
    fun deletePeriodTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.deletePeriodTransaction(periodTransactionStub))

            verify(transactionsRepository).deletePeriodTransaction(periodTransactionStub)
        }
    }

    @Test
    fun executePeriodTransactions() {
        runBlocking {
            val expectedTransactionsList = MutableLiveData<List<PeriodTransaction>>()
            expectedTransactionsList.value = periodTransactionsListStub
            val expectedDeferredTransactions = walletInteractor.getDeferredTransactions(periodTransactionsListStub)
            val expectedTransactionsSum = walletInteractor.transactionsSum(walletInteractor.getDeferredTransactions(periodTransactionsListStub))
            `when`(transactionsRepository.getPeriodTransactions()).thenReturn(expectedTransactionsList)

            assertNotNull(walletInteractor.executePeriodTransactions())

            verify(transactionsRepository).getPeriodTransactions()
            verify(transactionsRepository).addTransactions(expectedDeferredTransactions)
            verify(walletRepository).updateWalletAfterTransaction(transactionsListStub.first().walletId, expectedTransactionsSum)
        }
    }

    @Test
    fun getTemplateTransactions() {
        runBlocking {
            val expectedTemplateTransactions = MutableLiveData<List<TemplateTransaction>>()
            expectedTemplateTransactions.value = templateTransactionsListStub
            `when`(transactionsRepository.getTemplateTransactions()).thenReturn(expectedTemplateTransactions)

            assertEquals(expectedTemplateTransactions, walletInteractor.getTemplateTransactions())

            verify(transactionsRepository).getTemplateTransactions()
        }
    }

    @Test
    fun addTemplateTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.addTemplateTransaction(templateTransactionStub))

            verify(transactionsRepository).addTemplateTransaction(templateTransactionStub)
        }
    }

    @Test
    fun deleteTemplateTransaction() {
        runBlocking {
            assertNotNull(walletInteractor.deleteTemplateTransaction(templateTransactionStub))

            verify(transactionsRepository).deleteTemplateTransaction(templateTransactionStub)
        }
    }

    @Test
    fun getCurrencyRate() {
        runBlocking {
            val expectedCurrencyRate = MutableLiveData<Float>()
            expectedCurrencyRate.value = currencyRateStub
            `when`(currencyRateRepository.getCurrencyRate()).thenReturn(expectedCurrencyRate)

            assertEquals(expectedCurrencyRate, walletInteractor.getCurrencyRate())

            verify(currencyRateRepository).getCurrencyRate()
        }
    }


    @Test
    fun updateCurrencyRate() {
        runBlocking {
            assertNotNull(walletInteractor.updateCurrencyRate())

            verify(currencyRateRepository).updateCurrencyRate()
        }
    }

    @Test
    fun transactionsSum() {
        runBlocking {
            val transactions = listOf(transactionStub, transactionStub)
            var expectedSum = 0.0
            if (transactionStub.type == TransactionTypes.IN) {
                expectedSum += transactionStub.cost * 2
            } else {
                expectedSum -= transactionStub.cost * 2
            }

            assertEquals(walletInteractor.transactionsSum(transactions).toFloat(), expectedSum.toFloat())
        }
    }
}