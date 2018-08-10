package ru.yandex.moykoshelek.ui.report

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import ru.yandex.moykoshelek.interactors.WalletInteractor
import java.util.*
import javax.inject.Inject

class ReportViewModel @Inject constructor(private val walletInteractor: WalletInteractor) : ViewModel() {

    fun getReport(from: Date, to: Date) = async { walletInteractor.report(from, to) }

}