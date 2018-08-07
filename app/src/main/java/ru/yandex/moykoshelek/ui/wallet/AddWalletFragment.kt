package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_wallet.*
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.forEachChildWithIndex
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.WalletTypes
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject

class AddWalletFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_wallet
    override val TAG = Screens.ADD_WALLET_SCREEN

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: AddWalletViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(AddWalletViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallet_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                showWalletFields(position)
                icon_currency.visibility = View.VISIBLE
                wallet_currency_spinner.visibility = View.VISIBLE
                submit_button.visibility = View.VISIBLE
            }
        }
        submit_button.setOnClickListener { createWallet() }
    }

    private fun createWallet() {
        create_wallet_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = getString(R.string.fill_field)
                return
            }
        }
        val wallet = Wallet()
        with(wallet) {
            type = wallet_type_spinner.selectedItemPosition
            currency = wallet_currency_spinner.selectedItemPosition
            date = wallet_card_date.text.toString()
            name = wallet_name.text.toString()
            number = when (wallet.type) {
                WalletTypes.E_WALLET -> wallet_number.text.toString()
                WalletTypes.CREDIT_CARD -> wallet_card_number.text.toString()
                WalletTypes.BANK_ACCOUNT -> wallet_account_number.text.toString()
                else -> ""
            }
            viewModel.addWallet(this)
        }
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun showWalletFields(position: Int) {
        create_wallet_layout.forEachChildWithIndex { i, it ->
            if (i > 1) {
                it.visibility = View.GONE
            }
        }

        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE

        when (position) {
            0 -> {
                wallet_name.hint = getString(R.string.wallet_name)
                icon_wallet.visibility = View.VISIBLE
                wallet_number.visibility = View.VISIBLE
                wallet_number.hint = getString(R.string.wallet_number)
            }
            1 -> {
                wallet_name.hint = getString(R.string.wallet_owner)
                icon_card.visibility = View.VISIBLE
                wallet_card_number.visibility = View.VISIBLE
                wallet_card_number.hint = getString(R.string.card_number)
                icon_date.visibility = View.VISIBLE
                wallet_card_date.visibility = View.VISIBLE
                wallet_card_date.hint = getString(R.string.card_expires)
            }
            2 -> {
                wallet_name.hint = getString(R.string.wallet_name)
            }
            3 -> {
                wallet_name.hint = getString(R.string.account_name)
                icon_number.visibility = View.VISIBLE
                wallet_account_number.visibility = View.VISIBLE
                wallet_account_number.hint = getString(R.string.account_number)
            }
        }
    }
}