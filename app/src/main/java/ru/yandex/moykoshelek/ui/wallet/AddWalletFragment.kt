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
                hideAllItems()
                when (position) {
                    WalletTypes.BANK_ACCOUNT -> showBankAccountFields()
                    WalletTypes.CASH_MONEY -> showCashMoneyFields()
                    WalletTypes.CREDIT_CARD -> showCreditCardFields()
                    WalletTypes.E_WALLET -> showElectronWalletFields()
                }
                icon_currency.visibility = View.VISIBLE
                wallet_currency_spinner.visibility = View.VISIBLE
                submit_button.visibility = View.VISIBLE
            }
        }
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createWallet() }
    }

    private fun createWallet() {
        create_wallet_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = "Пожалуйста, запольните полье"
                return
            }
        }
        val wallet = Wallet()
        wallet.type = wallet_type_spinner.selectedItemPosition
        wallet.currency = wallet_currency_spinner.selectedItemPosition
        wallet.date = wallet_card_date.text.toString()
        wallet.name = wallet_name.text.toString()
        wallet.number = when (wallet.type) {
            WalletTypes.E_WALLET -> wallet_number.text.toString()
            WalletTypes.CREDIT_CARD -> wallet_card_number.text.toString()
            WalletTypes.BANK_ACCOUNT -> wallet_account_number.text.toString()
            else -> ""
        }
        viewModel.addWallet(wallet)
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun showElectronWalletFields() {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название кошелька"

        icon_wallet.visibility = View.VISIBLE
        wallet_number.visibility = View.VISIBLE
        wallet_number.hint = "Номер кошелька"
    }

    private fun showCreditCardFields() {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Имя владельца"

        icon_card.visibility = View.VISIBLE
        wallet_card_number.visibility = View.VISIBLE
        wallet_card_number.hint = "Номер карты"

        icon_date.visibility = View.VISIBLE
        wallet_card_date.visibility = View.VISIBLE
        wallet_card_date.hint = "Срок действия карты"
    }

    private fun showCashMoneyFields() {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название кошелька"
    }

    private fun showBankAccountFields() {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название банка или счета"

        icon_number.visibility = View.VISIBLE
        wallet_account_number.visibility = View.VISIBLE
        wallet_account_number.hint = "Номер счета"
    }

    private fun hideAllItems() {
        create_wallet_layout.forEachChildWithIndex { i, it ->
            if (i > 1) {
                it.visibility = View.GONE
            }
        }
    }
}