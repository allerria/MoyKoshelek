package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject


class AddTransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_transaction
    override val TAG = Screens.ADD_TRANSACTION_SCREEN

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: AddTransactionViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var cardAdapter: CardsPagerAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(AddTransactionViewModel::class.java)
        initObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        submit_button.setOnClickListener { createTransaction() }
    }

    private fun createTransaction() {
        for (i in 0 until add_transaction_layout.childCount)
            if (add_transaction_layout.getChildAt(i).visibility == View.VISIBLE && add_transaction_layout.getChildAt(i) is EditText && (add_transaction_layout.getChildAt(i) as EditText).text.isEmpty()) {
                (add_transaction_layout.getChildAt(i) as EditText).error = "Пожалуйста заполните поле"
                return
            }
        val transaction = Transaction()
        transaction.cost = transaction_amount.text.toString().toDouble()
        transaction.currency = transaction_currency_spinner.selectedItemPosition
        transaction.placeholder = "Moscow, Russia"
        transaction.typeTransaction = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
        val wallet = cardAdapter.getItem(cards_viewpager.currentItem)
        transaction.walletId = wallet.id
        transaction.category = transaction_category.text.toString()
        viewModel.addTransaction(transaction)
        var balanceChange = transaction.cost
        val curr = viewModel.currencyRate.value!!
        if (wallet.currency != transaction.currency)
            balanceChange = if (transaction.currency == CurrencyTypes.USD) transaction.cost * curr else transaction.cost / curr
        if (transaction.typeTransaction == TransactionTypes.IN)
            wallet.balance += balanceChange
        else
            wallet.balance -= balanceChange
        viewModel.updateWallet(wallet)
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun setupViewPager() {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        cards_viewpager.adapter = cardAdapter
        cards_viewpager.offscreenPageLimit = 3
        cards_viewpager.clipToPadding = false
        cards_viewpager.setPadding(96, 0, 96, 0)
        cards_viewpager.pageMargin = 48
    }


    private fun initObservers() {

        viewModel.categories.observe(this, Observer { categories ->
            val adapter = ArrayAdapter<String>(this@AddTransactionFragment.context, android.R.layout.simple_dropdown_item_1line, categories)
            transaction_category.setAdapter(adapter)
        })

        viewModel.wallets.observe(this, Observer { wallets ->
            cardAdapter.setData(wallets!!)
        })

    }

}