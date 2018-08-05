package ru.yandex.moykoshelek.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar_main.*
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
import ru.yandex.moykoshelek.ui.about.AboutFragment
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.settings.SettingsFragment
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity() {

    override val layoutRes = R.layout.activity_main

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            router.newRootScreen(Screens.BALANCE_SCREEN)
        }
        setSupportActionBar(toolbar)
        initToolbarIcon()
        supportFragmentManager.addOnBackStackChangedListener { initToolbarIcon() }
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
                    router.backTo(Screens.BALANCE_SCREEN)
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
            Screens.ABOUT_SCREEN -> AboutFragment()
            Screens.SETTINGS_SCREEN -> SettingsFragment()
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
                Screens.BALANCE_SCREEN -> {
                    toolbar.setNavigationIcon(R.drawable.ic_hamburger)
                }
                else -> {
                    toolbar.setNavigationIcon(R.drawable.ic_exit)
                }
            }
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
        }
    }
}
