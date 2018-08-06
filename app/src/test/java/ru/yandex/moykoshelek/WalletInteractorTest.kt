package ru.yandex.moykoshelek


import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.data.entities.WalletTypes
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

    val walletStub = Wallet(1, WalletTypes.BANK_ACCOUNT, "testwallet", 1000.0, CurrencyTypes.RUB, "2", "22/12")
    val walletListStub = listOf(walletStub, walletStub)
    val transactionStub = Transaction(1, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    val transactionsListStub = listOf(transactionStub, transactionStub)
    val currencyRateStub = 63.0f
    val periodTransactionStub = PeriodTransaction(1, getCurrentDateTimeBeforeDays(14), 7, 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    val periodTransactionStub1 = PeriodTransaction(2, getCurrentDateTimeBeforeDays(30), 10, 5.0, CurrencyTypes.RUB, "asd", TransactionTypes.OUT, 1, "agagaga")
    val periodTransactionsListStub = listOf(periodTransactionStub, periodTransactionStub1)

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
    fun getBalance() {
        val expectedWallets = MutableLiveData<List<Wallet>>()
        expectedWallets.value = walletListStub
        `when`(walletRepository.getWallets()).thenReturn(expectedWallets)

        assertEquals(walletInteractor.getWallets(), expectedWallets)

        verify(walletRepository).getWallets()
    }

    @Test
    fun getTransactions() {
        val expectedTransactions = MutableLiveData<List<Transaction>>()
        expectedTransactions.value = transactionsListStub
        `when`(transactionsRepository.getTransactions()).thenReturn(expectedTransactions)

        assertEquals(walletInteractor.getTransactions(), expectedTransactions)

        verify(transactionsRepository).getTransactions()
    }

    @Test
    fun getTransactionByWalletId() {
        val walletId = 1
        val expectedTransaction = MutableLiveData<List<Transaction>>()
        expectedTransaction.value = transactionsListStub
        `when`(transactionsRepository.getTransactionsByWalletId(walletId)).thenReturn(expectedTransaction)

        assertEquals(walletInteractor.getTransactions(walletId), expectedTransaction)

        verify(transactionsRepository).getTransactionsByWalletId(walletId)
    }

    @Test
    fun getCurrencyRate() {
        val expectedCurrencyRate = MutableLiveData<Float>()
        expectedCurrencyRate.value = currencyRateStub
        `when`(currencyRateRepository.getCurrencyRate()).thenReturn(expectedCurrencyRate)

        assertEquals(walletInteractor.getCurrencyRate(), expectedCurrencyRate)

        verify(currencyRateRepository).getCurrencyRate()
    }

    @Test
    fun addTransaction() {
        assertNotNull(walletInteractor.addTransaction(transactionStub))

        verify(transactionsRepository).addTransaction(transactionStub)
    }

    @Test
    fun addWallet() {
        assertNotNull(walletInteractor.addWallet(walletStub))

        verify(walletRepository).addWallet(walletStub)
    }

    @Test
    fun executeTransaction() {
        assertNotNull(walletInteractor.executeTransaction(transactionStub))

        verify(transactionsRepository).addTransaction(transactionStub)
        verify(walletRepository).updateWalletAfterTransaction(transactionStub.walletId, if (transactionStub.type == TransactionTypes.IN) transactionStub.cost else -transactionStub.cost)
    }

    @Test
    fun updateWallet() {
        assertNotNull(walletInteractor.updateWallet(walletStub))

        verify(walletRepository).updateWallet(walletStub)
    }

    @Test
    fun updateCurrencyRate() {
        assertNotNull(walletInteractor.updateCurrencyRate())

        verify(currencyRateRepository).updateCurrencyRate()
    }

    @Test
    fun addPeriodTransactionAndGetId() {

        assertNotNull(walletInteractor.addPeriodTransaction(periodTransactionStub))

        verify(transactionsRepository).addPeriodTransaction(periodTransactionStub)
    }

    @Test
    fun getPeriodTransaction() {
        val periodTransactionId = 1
        val expectedPeriodTransaction = periodTransactionStub
        `when`(transactionsRepository.getPeriodTransaction(periodTransactionId)).thenReturn(periodTransactionStub)

        assertEquals(walletInteractor.getPeriodTransaction(periodTransactionId), expectedPeriodTransaction)

        verify(transactionsRepository).getPeriodTransaction(periodTransactionId)
    }

    @Test
    fun executePeriodTransactions() {
        `when`(transactionsRepository.getPeriodTransactions()).thenReturn(periodTransactionsListStub)
        assertNotNull(walletInteractor.executePeriodTransactions())
        verify(transactionsRepository).addTransactions(walletInteractor.getDeferredTransactions(periodTransactionsListStub))
        verify(walletRepository).updateWalletAfterTransaction(transactionsListStub.first().walletId, walletInteractor.transactionsSum(walletInteractor.getDeferredTransactions(periodTransactionsListStub)))
    }

    @Test
    fun getDeferredTransactions() {
        val periodTransactions = listOf(periodTransactionStub)
        val cal = Calendar.getInstance()
        cal.time = periodTransactionStub.time
        cal.add(Calendar.DAY_OF_MONTH, periodTransactionStub.period)
        val time1 = cal.time
        cal.add(Calendar.DAY_OF_MONTH, periodTransactionStub.period)
        val time2 = cal.time
        val expectedTransactions = listOf(Transaction(0, time1, 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto"), Transaction(0, time2, 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto"))
        assertEquals(walletInteractor.getDeferredTransactions(periodTransactions), expectedTransactions)
    }

    @Test
    fun transactionsSum() {
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