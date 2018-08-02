package ru.yandex.moykoshelek.ui.transaction

import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.data.datasource.local.entities.TransactionData
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.balance.BalanceViewModel
import javax.inject.Inject


class AddTransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_transaction
    override val TAG = Screens.ADD_TRANSACTION_SCREEN

    private lateinit var layout: ConstraintLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var cardAdapter: CardsPagerAdapter

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: AddTransactionViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(AddTransactionViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe()
        setupViewPager(view)
        layout = view.findViewById(R.id.add_transaction_layout)
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createTransaction(view) }
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun createTransaction(view: View) {
        for (i in 0 until layout.childCount)
            if (layout.getChildAt(i).visibility == View.VISIBLE && layout.getChildAt(i) is EditText && (layout.getChildAt(i) as EditText).text.isEmpty()) {
                (layout.getChildAt(i) as EditText).error = "Пожалуйста, запольните полье"
                return
            }
        val transaction = TransactionData()
        transaction.cost = view.findViewById<EditText>(R.id.transaction_amount).text.toString().toDouble()
        transaction.currency = view.findViewById<Spinner>(R.id.transaction_currency_spinner).selectedItemPosition
        transaction.placeholder = "Moscow, Russia"
        transaction.typeTransaction = if (view.findViewById<RadioButton>(R.id.in_radio).isChecked) TransactionTypes.IN else TransactionTypes.OUT
        val wallet = cardAdapter.getItem(view.findViewById<ViewPager>(R.id.cards_viewpager).currentItem)
        transaction.walletId = wallet.id?.toInt()
        transaction.category = transaction_category.selectedItem.toString()
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

    private fun setupViewPager(view: View) {
        viewPager = view.findViewById(R.id.cards_viewpager)
        tabLayout = view.findViewById(R.id.tab_dots) as TabLayout
        tabLayout.setupWithViewPager(viewPager, true)
        cardAdapter = CardsPagerAdapter()
        viewPager.adapter = cardAdapter
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.setPadding(96, 0, 96, 0)
        viewPager.pageMargin = 48
    }


    private fun subscribe() {

        viewModel.categories.observe(this, Observer { categories ->
            val adapter = ArrayAdapter<String>(this@AddTransactionFragment.context, android.R.layout.simple_dropdown_item_1line, categories)
            val textView = transaction_category
            textView.adapter = adapter
        })

        viewModel.wallets.observe(this, Observer { wallets ->
            cardAdapter.setData(wallets!!)
        })

    }

    private fun removeObservers() {
        viewModel.categories.removeObservers(this)
        viewModel.wallets.removeObservers(this)
    }
}