package ru.yandex.moykoshelek.data.datasource.remote

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import timber.log.Timber

fun CurrencyRateRemote(): Float? {
    var currencyRate: Float? = null
    AndroidNetworking.get("https://free.currencyconverterapi.com/api/v6/convert?q=USD_RUB&compact=y")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val currency: Float = response.getJSONObject("USD_RUB").getDouble("val").toFloat()
                    Timber.d(currency.toString())
                    currencyRate = currency
                }

                override fun onError(error: ANError) {
                    Timber.e(error.errorBody)
                }
            })
    return currencyRate
}