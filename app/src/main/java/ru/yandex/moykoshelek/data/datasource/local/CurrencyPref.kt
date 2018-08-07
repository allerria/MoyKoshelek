package ru.yandex.moykoshelek.data.datasource.local

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import javax.inject.Inject

class CurrencyPref(app: Context) {
    companion object {
        private const val defaultConvert = 62.0F
        private const val currConvert = "currentConvert"
    }

    private var prefs = getDefaultSharedPreferences(app)

    fun getCurrentConvert(): LiveData<Float> {
        val result = MutableLiveData<Float>()
        result.value = this.prefs.getFloat(currConvert, defaultConvert)
        return result
    }

    fun setCurrentConvert(currentConvert: Float) {
        val editor = this.prefs.edit()
        editor.putFloat(currConvert, currentConvert)
        editor.apply()
    }
}