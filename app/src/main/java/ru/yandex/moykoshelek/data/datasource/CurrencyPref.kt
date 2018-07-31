package ru.yandex.moykoshelek.data.datasource

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences

class CurrencyPref(context: Context){

    private var prefs = getDefaultSharedPreferences(context)
    private val currConvert = "currentConvert"
    private val defaultConvert = 62.0F

    fun getCurrentConvert(): Float {
        return this.prefs.getFloat(this.currConvert, this.defaultConvert)
    }

    fun setCurrentConvert(currentConvert: Float) {
        val editor = this.prefs.edit()
        editor.putFloat(this.currConvert, currentConvert)
        editor.apply()
    }
}