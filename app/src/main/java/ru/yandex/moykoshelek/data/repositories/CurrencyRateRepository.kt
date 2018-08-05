package ru.yandex.moykoshelek.data.repositories

import android.arch.lifecycle.LiveData
import ru.yandex.moykoshelek.data.datasource.local.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.remote.CurrencyRateRemote
import javax.inject.Inject

class CurrencyRateRepository @Inject constructor(private val currencyPref: CurrencyPref) {

    fun getCurrencyRate(): LiveData<Float> = currencyPref.getCurrentConvert()

    fun updateCurrencyRate() {
        CurrencyRateRemote().observeForever {
            currencyPref.setCurrentConvert(it!!)
        }
    }

}