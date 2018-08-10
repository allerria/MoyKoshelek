package ru.yandex.moykoshelek

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.yandex.moykoshelek.data.datasource.local.dao.TemplateTransactionDao
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.util.TestUtils.getValueFromLiveData

class TemplateTranDaoTest : DbTest() {

    private lateinit var templateTranDao: TemplateTransactionDao
    private val templateTransactionStub = TemplateTransaction(1, "template", null, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val templateTransactionStub1 = TemplateTransaction(2, "template", 7, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, TransactionTypes.IN, 1, "auto")
    private val templateTransactionsListStub = listOf(templateTransactionStub, templateTransactionStub1)

    @Before
    fun setUp() {
        templateTranDao = appDatabase.templateTransactionDao
    }

    @Test
    fun insertAndGetAll() {
        runBlocking {
            assertNotNull(templateTranDao.insert(templateTransactionStub))
            assertNotNull(templateTranDao.insert(templateTransactionStub1))

            assertEquals(templateTransactionsListStub, getValueFromLiveData(templateTranDao.getAll()))
        }
    }

    @Test
    fun delete() {
        runBlocking {
            assertNotNull(templateTranDao.insert(templateTransactionStub))

            assertNotNull(templateTranDao.delete(templateTransactionStub))

            assertEquals(listOf<List<TemplateTransaction>>(), getValueFromLiveData(templateTranDao.getAll()))
        }
    }

}