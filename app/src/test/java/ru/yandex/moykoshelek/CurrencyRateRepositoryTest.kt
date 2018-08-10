package ru.yandex.moykoshelek

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.mockito.Mockito.*
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.remote.CurrencyRateRemote
import ru.yandex.moykoshelek.data.repositories.CurrencyRateRepository

@RunWith(JUnit4::class)
class CurrencyRateRepositoryTest {

    private lateinit var currencyRateRepository: CurrencyRateRepository
    private lateinit var currencyPref: CurrencyPref
    private lateinit var currencyRateRemote: CurrencyRateRemote
    private val currencyRateStub = 62.5f

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        currencyPref = mock(CurrencyPref::class.java)
        currencyRateRemote = mock(CurrencyRateRemote::class.java)
        currencyRateRepository = CurrencyRateRepository(currencyPref, currencyRateRemote)
    }

    @Test
    fun getCurrencyRate() {
        runBlocking {
            val expectedCurrencyRate = MutableLiveData<Float>()
            expectedCurrencyRate.value = currencyRateStub
            `when`(currencyPref.getCurrentConvert()).thenReturn(expectedCurrencyRate)

            assertEquals(expectedCurrencyRate, currencyRateRepository.getCurrencyRate())

            verify(currencyPref).getCurrentConvert()
        }
    }

    @Test
    fun updateCurrencyRate() {
        runBlocking {
            `when`(currencyRateRemote.getCurrencyRate()).thenReturn(currencyRateStub)

            assertNotNull(currencyRateRepository.updateCurrencyRate())

            verify(currencyPref).setCurrentConvert(currencyRateStub)
        }
    }
}