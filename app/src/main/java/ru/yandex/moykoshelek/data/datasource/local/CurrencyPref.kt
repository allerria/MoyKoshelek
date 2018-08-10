package ru.yandex.moykoshelek.data.datasource.local

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences

class CurrencyPref(private val prefs: SharedPreferences) {
    companion object {
        private const val defaultConvert = 62.0F
        private const val currConvert = "currentConvert"
    }

    fun getCurrentConvert(): LiveData<Float> {
        val result = MutableLiveData<Float>()
        result.value = prefs.getFloat(currConvert, defaultConvert)
        return result
    }

    fun setCurrentConvert(currentConvert: Float) {
        val editor = prefs.edit()
        editor.putFloat(currConvert, currentConvert)
        editor.apply()
    }
}