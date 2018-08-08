package ru.yandex.moykoshelek

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.yandex.moykoshelek.data.datasource.local.dao.PeriodTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTimeBeforeDays

class PeriodTranDaoTest: DbTest() {

    private lateinit var periodTransactionDao: PeriodTransactionDao
    private val periodTransactionStub = PeriodTransaction(1, getCurrentDateTimeBeforeDays(14), 7, 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    private val periodTransactionStub1 = PeriodTransaction(2, getCurrentDateTimeBeforeDays(30), 10, 5.0, CurrencyTypes.RUB, "asd", TransactionTypes.OUT, 1, "agagaga")
    private val periodTransactionsListStub = listOf(periodTransactionStub, periodTransactionStub1)

    @Before
    fun setUp() {
        periodTransactionDao = appDatabase.periodTransactionDao
    }

    @Test
    fun insertAndGetById() {
        runBlocking {
            assertNotNull(periodTransactionDao.insert(periodTransactionStub))

            assertEquals(periodTransactionStub, periodTransactionDao.getById(periodTransactionStub.id))
        }
    }

    @Test
    fun getAll() {
        runBlocking {
            periodTransactionsListStub.forEach {
                assertNotNull(periodTransactionDao.insert(it))
            }

            assertEquals(periodTransactionsListStub, periodTransactionDao.getAll())
        }
    }

}