package ru.yandex.moykoshelek.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker
import com.treebo.internetavailabilitychecker.InternetConnectivityListener
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.selector
import org.json.JSONObject
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.models.database.AppDatabase
import ru.yandex.moykoshelek.transaction.AddTransactionFragment
import ru.yandex.moykoshelek.wallet.AddWalletFragment
import ru.yandex.moykoshelek.balance.BalanceFragment
import ru.yandex.moykoshelek.menu.MenuFragment
import ru.yandex.moykoshelek.common.CurrencyPref
import ru.yandex.moykoshelek.common.extensions.hideKeyboard
import ru.yandex.moykoshelek.common.BaseActivity
import ru.yandex.moykoshelek.common.DbWorkerThread
import ru.yandex.moykoshelek.common.Screens


class MainActivity : BaseActivity(), InternetConnectivityListener {
    override val layoutRes = R.layout.activity_main

    private var isMenuShowed = false
    var appDb: AppDatabase? = null
    lateinit var dbWorkerThread: DbWorkerThread
    val uiHandler = Handler()
    private lateinit var internetAvailabilityChecker: InternetAvailabilityChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_hamburger)
        appDb = AppDatabase.getInstance(this)
        onInternetConnectivityChanged(true)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            async(UI) {
                bg {
                    getCurrencyFromInternet()
                }
            }
        }
    }

    private fun getCurrencyFromInternet() {
        AndroidNetworking.get("https://free.currencyconverterapi.com/api/v6/convert?q=USD_RUB&compact=y")
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        val currency: Float = response.getJSONObject("USD_RUB").getDouble("val").toFloat()
                        CurrencyPref(this@MainActivity).setCurrentConvert(currency)
                    }

                    override fun onError(error: ANError) {
                        Log.e("CurrencyError", error.errorBody)
                    }
                })
    }

    override fun onStart() {
        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance()
        internetAvailabilityChecker.addInternetConnectivityListener(this)
        super.onStart()
    }

    override fun onStop() {
        dbWorkerThread.interrupt()
        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this)
        super.onStop()
    }

    override fun onBackPressed() {
        if (isMenuShowed) {
            showOrHideMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> showSelectAddDialog()
            android.R.id.home -> showOrHideMenu()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override val navigator = object : SupportAppNavigator(this, R.id.main_container) {

        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = null

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.ADD_TRANSACTION_SCREEN -> AddTransactionFragment()
            Screens.ADD_WALLET_SCREEN -> AddWalletFragment()
            else -> null
        }
    }

    private fun showSelectAddDialog() {
        val array = arrayOf("Счет", "Доход/Расход")
        selector("Выберите что добавить", array.toList()) { _, i ->
            run {
                when (i) {
                    0 -> showFragment(Screens.ADD_WALLET_SCREEN, true)
                    1 -> showFragment(Screens.ADD_TRANSACTION_SCREEN, true)
                }
            }
        }
    }

    fun showOrHideMenu() {
        if (isMenuShowed) {
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
            super.onBackPressed()
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_exit)
            showFragment(Screens.MENU_SCREEN, true)
        }
        isMenuShowed = !isMenuShowed
    }

    fun showFragment(fragmentCode: String, addBackStack: Boolean) {
        this.hideKeyboard()
        if (isMenuShowed) showOrHideMenu()
        var transaction = supportFragmentManager.beginTransaction()
        transaction = when (fragmentCode) {
            Screens.ADD_TRANSACTION_SCREEN -> {
                setActionBarTitle("Добавить кошелек")
                transaction.replace(R.id.main_container, AddTransactionFragment())
            }
            Screens.ADD_WALLET_SCREEN -> {
                setActionBarTitle("Добавить кошелек")
                transaction.replace(R.id.main_container, AddWalletFragment())
            }
            Screens.MENU_SCREEN -> {
                setActionBarTitle("Мой меню")
                transaction.add(R.id.main_container, MenuFragment())
            }
            else -> {
                setActionBarTitle("Мой кошелёк")
                transaction.replace(R.id.main_container, BalanceFragment())
            }
        }
        if (addBackStack)
            transaction = transaction.addToBackStack("fragment$fragmentCode")
        transaction.commit()
    }

    private fun setActionBarTitle(title: String) {
        val s = SpannableString(title)
        if (s.indexOf(' ') != -1) {
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.toolbar_text_red)), 0, s.indexOf(' '), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.toolbar_text_black)), s.indexOf(' '), s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.colorAccent)), 0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar?.title = s
    }
}
