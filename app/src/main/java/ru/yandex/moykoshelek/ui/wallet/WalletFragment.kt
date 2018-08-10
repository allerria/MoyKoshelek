package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.forEachChildWithIndex
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.showSuccessToast
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.transaction.ActionTypes
import timber.log.Timber
import javax.inject.Inject

class WalletFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_wallet
    override val TAG = Screens.WALLET_SCREEN

    companion object {
        fun getInstance(walletId: Int?) = WalletFragment().apply {
            arguments = Bundle().apply {
                if (walletId != null)
                    putInt(TAG, walletId)
            }
        }
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: WalletViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var wallet: Wallet? = null
    private var walletId = 0

    private var dialogClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                viewModel.deleteWallet(wallet!!)
                router.exit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel::class.java)
        if (arguments != null) {
            walletId = arguments!!.getInt(TAG)
        }
        submit_button.setOnClickListener { createOrUpdateWallet() }
        if (walletId > 0) {
            initObserve()
            delete_button.setOnClickListener { createDeleteDialog() }
            submit_button.text = getString(R.string.edit)
            wallet_currency_spinner.visibility = View.GONE
            icon_currency.visibility = View.GONE
        } else {
            delete_button.visibility = View.GONE
        }
    }

    private fun createOrUpdateWallet() {
        create_wallet_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = getString(R.string.fill_field)
                return
            }
        }
        if (wallet == null) {
            wallet = Wallet()
        }
        with(wallet!!) {
            name = wallet_name.text.toString()
            balance = wallet_balance.text.toString().toDouble()
            if (wallet!!.id > 0) {
                viewModel.updateWallet(this)
            } else {
                currency = wallet_currency_spinner.selectedItemPosition
                viewModel.addWallet(this)
            }
        }
        showSuccessToast()
        router.exit()
    }

    private fun initObserve() = launch {
        viewModel.getWallet(walletId).await().observe(this@WalletFragment, Observer { wallet ->
            if (wallet != null) {
                this@WalletFragment.wallet = wallet
                setWallet()
            }
        })
    }

    private fun setWallet() {
        wallet_name.setText(wallet!!.name)
        wallet_balance.setText(wallet!!.balance.toString())
    }

    private fun createDeleteDialog() {
        AlertDialog.Builder(this@WalletFragment.context!!)
                .setMessage(getString(R.string.are_you_sure_to_delete))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show()
    }
}