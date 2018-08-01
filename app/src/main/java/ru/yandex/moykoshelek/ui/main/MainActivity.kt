package ru.yandex.moykoshelek.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
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
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.selector
import org.json.JSONObject
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.transaction.AddTransactionFragment
import ru.yandex.moykoshelek.ui.wallet.AddWalletFragment
import ru.yandex.moykoshelek.ui.balance.BalanceFragment
import ru.yandex.moykoshelek.ui.menu.MenuFragment
import ru.yandex.moykoshelek.interactors.WalletInteractor
import ru.yandex.moykoshelek.ui.common.BaseActivity
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.common.BaseFragment
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity(), InternetConnectivityListener {

    override val layoutRes = R.layout.activity_main
    val uiHandler = Handler()
    private lateinit var internetAvailabilityChecker: InternetAvailabilityChecker

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var walletInteractor: WalletInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            router.newRootScreen(Screens.BALANCE_SCREEN)
        }
        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance()
        internetAvailabilityChecker.addInternetConnectivityListener(this)
        setSupportActionBar(toolbar)
        initToolbarIcon()
        supportFragmentManager.addOnBackStackChangedListener { initToolbarIcon() }
        onInternetConnectivityChanged(true)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected) {
            launch {
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
                        walletInteractor.setCurrencyRate(currency)
                    }

                    override fun onError(error: ANError) {
                        Log.e("CurrencyError", error.errorBody)
                    }
                })
    }

    override fun onStop() {
        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> showSelectAddDialog()
            android.R.id.home -> {
                if ((supportFragmentManager.fragments.first() as BaseFragment).TAG != Screens.BALANCE_SCREEN) {
                    router.exit()
                } else {
                    router.navigateTo(Screens.MENU_SCREEN)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override val navigator = object : SupportAppNavigator(this, R.id.main_container) {

        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = null

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.BALANCE_SCREEN -> BalanceFragment()
            Screens.MENU_SCREEN -> MenuFragment()
            Screens.ADD_TRANSACTION_SCREEN -> AddTransactionFragment()
            Screens.ADD_WALLET_SCREEN -> AddWalletFragment()
            else -> null
        }

    }

    private fun showSelectAddDialog() {
        val array = arrayOf("Счет", "Доход/Расход")
        selector("Выберите что добавить", array.toList()) { _, i ->
            when (i) {
                0 -> router.navigateTo(Screens.ADD_WALLET_SCREEN)
                1 -> router.navigateTo(Screens.ADD_TRANSACTION_SCREEN)
            }
        }
    }

    private fun initToolbarIcon() {
        if (supportFragmentManager.fragments.isNotEmpty()) {
            Timber.d((supportFragmentManager.fragments.first() as BaseFragment).TAG)
            when ((supportFragmentManager.fragments.first() as BaseFragment).TAG) {
                Screens.BALANCE_SCREEN -> toolbar.setNavigationIcon(R.drawable.ic_hamburger)
                else -> toolbar.setNavigationIcon(R.drawable.ic_exit)
            }
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
        }
    }
}
