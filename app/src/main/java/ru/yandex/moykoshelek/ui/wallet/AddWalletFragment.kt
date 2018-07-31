package ru.yandex.moykoshelek.ui.wallet

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.ui.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData
import ru.yandex.moykoshelek.data.entities.WalletTypes
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject

class AddWalletFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_wallet
    override val TAG = Screens.ADD_WALLET_SCREEN

    lateinit var layout:ConstraintLayout

    @Inject
    lateinit var router: Router

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layout = view.findViewById(R.id.create_wallet_layout)
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Spinner>(R.id.wallet_type_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                hideAllItems()
                when(position) {
                    WalletTypes.BANK_ACCOUNT -> showBankAccountFields(view)
                    WalletTypes.CASH_MONEY -> showCashMoneyFields(view)
                    WalletTypes.CREDIT_CARD -> showCreditCardFields(view)
                    WalletTypes.E_WALLET -> showElectronWalletFields(view)
                }
                view.findViewById<ImageView>(R.id.icon_currency).visibility = View.VISIBLE
                view.findViewById<Spinner>(R.id.wallet_currency_spinner).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.submit_button).visibility = View.VISIBLE
            }
        }
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createWallet(view) }
    }

    private fun createWallet(view: View) {
        for (i in 0 until layout.childCount)
            if (layout.getChildAt(i).visibility == View.VISIBLE && layout.getChildAt(i) is EditText && (layout.getChildAt(i) as EditText).text.isEmpty()) {
                (layout.getChildAt(i) as EditText).error = "Пожалуйста, запольните полье"
                return
            }
        val wallet = WalletData()
        wallet.type = view.findViewById<Spinner>(R.id.wallet_type_spinner).selectedItemPosition
        wallet.currency = view.findViewById<Spinner>(R.id.wallet_currency_spinner).selectedItemPosition
        wallet.date = view.findViewById<EditText>(R.id.wallet_card_date).text.toString()
        wallet.name = view.findViewById<EditText>(R.id.wallet_name).text.toString()
        wallet.number = when(wallet.type){
            WalletTypes.E_WALLET -> view.findViewById<EditText>(R.id.wallet_number).text.toString()
            WalletTypes.CREDIT_CARD -> view.findViewById<EditText>(R.id.wallet_card_number).text.toString()
            WalletTypes.BANK_ACCOUNT -> view.findViewById<EditText>(R.id.wallet_account_number).text.toString()
            else -> ""
        }
        insertWalletDataInDb(wallet)
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun insertWalletDataInDb(data: WalletData) {
        val task = Runnable { (activity as MainActivity).appDb?.walletDataDao()?.insert(data) }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun showElectronWalletFields(view: View) {
        view.findViewById<ImageView>(R.id.icon_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).hint = "Название кошелька"

        view.findViewById<ImageView>(R.id.icon_wallet).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_number).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_number).hint = "Номер кошелька"
    }

    private fun showCreditCardFields(view: View) {
        view.findViewById<ImageView>(R.id.icon_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).hint = "Имя владельца"

        view.findViewById<ImageView>(R.id.icon_card).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_card_number).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_card_number).hint = "Номер карты"

        view.findViewById<ImageView>(R.id.icon_date).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_card_date).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_card_date).hint = "Срок действия карты"
    }

    private fun showCashMoneyFields(view: View) {
        view.findViewById<ImageView>(R.id.icon_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).hint = "Название кошелька"
    }

    private fun showBankAccountFields(view: View) {
        view.findViewById<ImageView>(R.id.icon_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_name).hint = "Название банка или счета"

        view.findViewById<ImageView>(R.id.icon_number).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_account_number).visibility = View.VISIBLE
        view.findViewById<EditText>(R.id.wallet_account_number).hint = "Номер счета"
    }

    private fun hideAllItems() {
        for (i in 3 until layout.childCount){
            val child = layout.getChildAt(i)
            child.visibility = View.GONE
        }
    }
}