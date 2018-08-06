package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.remote.CurrencyRateRemote
import javax.inject.Inject

class CurrencyRateRepository(private val currencyPref: CurrencyPref, private val currencyRateRemote: CurrencyRateRemote) {

    fun getCurrencyRate(): LiveData<Float> = currencyPref.getCurrentConvert()

    fun updateCurrencyRate() {
        val currencyRate = currencyRateRemote.getCurrencyRate()
        if (currencyRate != null) {
            currencyPref.setCurrentConvert(currencyRate)
        }
    }

}