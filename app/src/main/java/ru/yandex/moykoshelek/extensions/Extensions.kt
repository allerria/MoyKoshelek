package ru.yandex.moykoshelek.extensions

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import java.text.SimpleDateFormat
import java.util.*

private val locale = Locale.getDefault()

fun Date.format(): String {
    val formatter = SimpleDateFormat("yyyy/MMMM/dd HH:mm:ss", locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getCurrentDateTimeBeforeDays(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, -days)
    return cal.time
}

fun Activity.hideKeyboard() {
    if (window.currentFocus != null) {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.currentFocus.windowToken, 0)
    }
}

fun Double.formatMoney(currencyType: Int) = String.format("%.2f", this) + if (CurrencyTypes.USD == currencyType) "$ " else "\u20BD "

fun transactionTypeSign(transactionType: Int) = if (transactionType == TransactionTypes.IN) "+" else "-"

fun Fragment.showSuccessToast() {
    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}