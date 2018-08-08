package ru.yandex.moykoshelek

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.util.TestUtils.getValue

@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner::class)
class CurrencyPrefTest {
    @Test
    fun setAndGetCurrencyRate() {
        val prefs = RuntimeEnvironment.application.getSharedPreferences("default", 0)
        val currencyPref = CurrencyPref(prefs)
        val expectedCurrencyRate = 63.2f

        assertNotNull(currencyPref.setCurrentConvert(expectedCurrencyRate))

        assertEquals(expectedCurrencyRate, getValue(currencyPref.getCurrentConvert()))
    }
}