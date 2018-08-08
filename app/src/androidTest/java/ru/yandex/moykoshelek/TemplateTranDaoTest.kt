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
import ru.yandex.moykoshelek.util.TestUtils.getValue

class TemplateTranDaoTest: DbTest() {

    private lateinit var templateTranDaoTest: TemplateTransactionDao
    private val templateTransactionStub = TemplateTransaction(1, "template", null, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    private val templateTransactionStub1 = TemplateTransaction(2, "template", 7, getCurrentDateTime(), 1.0, CurrencyTypes.RUB, "asd", TransactionTypes.IN, 1, "auto")
    private val templateTransactionsListStub = listOf(templateTransactionStub, templateTransactionStub1)

    @Before
    fun setUp() {
        templateTranDaoTest = appDatabase.templateTransactionDao
    }

    @Test
    fun insertAndGetAll() {
        runBlocking {
            assertNotNull(templateTranDaoTest.insert(templateTransactionStub))
            assertNotNull(templateTranDaoTest.insert(templateTransactionStub1))

            assertEquals(templateTransactionsListStub, getValue(templateTranDaoTest.getAll()))
        }
    }

}