package ru.yandex.moykoshelek.ui.menu

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_menu.*
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject

class MenuFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_menu
    override val TAG = Screens.MENU_SCREEN

    @Inject
    lateinit var router: Router

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menu_home.setOnClickListener { router.backTo(Screens.BALANCE_SCREEN) }
        menu_statistics.setOnClickListener { router.backTo(Screens.REPORT_SCREEN) }
        menu_settings.setOnClickListener { router.navigateTo(Screens.SETTINGS_SCREEN) }
        menu_about.setOnClickListener { router.navigateTo(Screens.ABOUT_SCREEN) }
        menu_layout.setOnClickListener { router.exit() }
    }

}