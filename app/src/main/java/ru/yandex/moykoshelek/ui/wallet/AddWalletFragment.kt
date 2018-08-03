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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallet_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                hideAllItems()
                when (position) {
                    WalletTypes.BANK_ACCOUNT -> showBankAccountFields(view)
                    WalletTypes.CASH_MONEY -> showCashMoneyFields(view)
                    WalletTypes.CREDIT_CARD -> showCreditCardFields(view)
                    WalletTypes.E_WALLET -> showElectronWalletFields(view)
                }
                icon_currency.visibility = View.VISIBLE
                wallet_currency_spinner.visibility = View.VISIBLE
                submit_button.visibility = View.VISIBLE
            }
        }
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createWallet(view) }
    }

    private fun createWallet(view: View) {
        for (i in 0 until create_wallet_layout.childCount)
            if (create_wallet_layout.getChildAt(i).visibility == View.VISIBLE && create_wallet_layout.getChildAt(i) is EditText && (create_wallet_layout.getChildAt(i) as EditText).text.isEmpty()) {
                (create_wallet_layout.getChildAt(i) as EditText).error = "Пожалуйста, запольните полье"
                return
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

    private fun showElectronWalletFields(view: View) {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название кошелька"

        icon_wallet.visibility = View.VISIBLE
        wallet_number.visibility = View.VISIBLE
        wallet_number.hint = "Номер кошелька"
    }

    private fun showCreditCardFields(view: View) {
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

    private fun showCashMoneyFields(view: View) {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название кошелька"
    }

    private fun showBankAccountFields(view: View) {
        icon_name.visibility = View.VISIBLE
        wallet_name.visibility = View.VISIBLE
        wallet_name.hint = "Название банка или счета"

        icon_number.visibility = View.VISIBLE
        wallet_account_number.visibility = View.VISIBLE
        wallet_account_number.hint = "Номер счета"
    }

    private fun hideAllItems() {
        for (i in 3 until create_wallet_layout.childCount) {
            val child = create_wallet_layout.getChildAt(i)
            child.visibility = View.GONE
        }
    }
}