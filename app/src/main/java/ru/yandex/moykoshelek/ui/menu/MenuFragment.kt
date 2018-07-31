package ru.yandex.moykoshelek.ui.menu

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_menu.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens

class MenuFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_menu
    override val TAG = Screens.MENU_SCREEN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menu_home.setOnClickListener {  }
        menu_statistics.setOnClickListener {  }
        menu_settings.setOnClickListener {  }
        menu_about.setOnClickListener {  }
        menu_layout.setOnClickListener { }
    }
}