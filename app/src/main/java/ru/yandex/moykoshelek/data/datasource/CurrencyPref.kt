package ru.yandex.moykoshelek.data.datasource

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import javax.inject.Inject

class CurrencyPref @Inject constructor(app: Application){

    private var prefs = getDefaultSharedPreferences(app)
    private val currConvert = "currentConvert"
    private val defaultConvert = 62.0F

    fun getCurrentConvert(): LiveData<Float> {
        val result = MutableLiveData<Float>()
        result.value = this.prefs.getFloat(this.currConvert, this.defaultConvert)
        return result
    }


    fun setCurrentConvert(currentConvert: Float) {
        val editor = this.prefs.edit()
        editor.putFloat(this.currConvert, currentConvert)
        editor.apply()
    }
}