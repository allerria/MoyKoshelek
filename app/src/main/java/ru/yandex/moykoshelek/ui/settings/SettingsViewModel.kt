package ru.yandex.moykoshelek.ui.settings

import android.arch.lifecycle.ViewModel
import ru.yandex.moykoshelek.interactors.WalletInteractor
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val walletInteractor: WalletInteractor): ViewModel()