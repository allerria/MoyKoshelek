package ru.yandex.moykoshelek.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Replace
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.transaction.TransactionFragment
import ru.yandex.moykoshelek.ui.wallet.WalletFragment
import ru.yandex.moykoshelek.ui.balance.BalanceFragment
import ru.yandex.moykoshelek.ui.menu.MenuFragment
import ru.yandex.moykoshelek.ui.common.BaseActivity
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.about.AboutFragment
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.report.ReportFragment
import ru.yandex.moykoshelek.ui.settings.SettingsFragment
import ru.yandex.moykoshelek.ui.transaction.PeriodTemplateTransactionsFragment
import ru.yandex.moykoshelek.ui.transaction.TransactionsFragment
import ru.yandex.moykoshelek.ui.wallet.WalletsFragment
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
            Screens.TRANSACTION_SCREEN -> TransactionFragment.getInstance(data as ArrayList<Int>)
            Screens.TRANSACTIONS_SCREEN -> TransactionsFragment.getInstance(data as Int)
            Screens.WALLET_SCREEN -> WalletFragment.getInstance(data as Int?)
            Screens.WALLETS_SCREEN -> WalletsFragment()
            Screens.ABOUT_SCREEN -> AboutFragment()
            Screens.SETTINGS_SCREEN -> SettingsFragment()
            Screens.REPORT_SCREEN -> ReportFragment()
            Screens.PERIOD_TEMPLATE_SCREEN -> PeriodTemplateTransactionsFragment()
            else -> null
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment?,
                                                       fragmentTransaction: FragmentTransaction) {
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun initToolbarIcon() {
        if (supportFragmentManager.fragments.isNotEmpty()) {
            Timber.d((supportFragmentManager.fragments.first() as BaseFragment).TAG)
            when ((supportFragmentManager.fragments.first() as BaseFragment).TAG) {
                Screens.BALANCE_SCREEN -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setDisplayShowHomeEnabled(false)
                    toolbar.setNavigationIcon(R.drawable.ic_hamburger)
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setDisplayShowHomeEnabled(true)
                }
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
        }
    }
}
