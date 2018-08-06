package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.remote.CurrencyRateRemote
import javax.inject.Inject

class CurrencyRateRepository(private val currencyPref: CurrencyPref) {

    fun getCurrencyRate(): LiveData<Float> = currencyPref.getCurrentConvert()

    fun updateCurrencyRate() {
        val currencyRate = CurrencyRateRemote()
        if (currencyRate != null) {
            currencyPref.setCurrentConvert(currencyRate)
        }
    }

}