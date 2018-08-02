package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_balance.*
import ru.yandex.moykoshelek.ui.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.WalletData
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject

class BalanceFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_balance
    override val TAG = Screens.BALANCE_SCREEN

    private lateinit var cardAdapter: CardsPagerAdapter
    private lateinit var transactionAdapter: MainListAdapter

    @Inject
    lateinit var viewModel: BalanceViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(BalanceViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView(view)
        setupViewPager(view)
        subscribe()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun subscribe() {
        viewModel.transactions.observe(this, Observer { transactions ->
            if (transactions != null) {
                transactionAdapter.setData(transactions)
            }
        })

        viewModel.wallets.observe(this, Observer { wallets ->
            if (wallets != null) {
                var sumOfDollar = 0.0
                var sumOfRuble = 0.0
                cardAdapter.setData(wallets)
                val curr = viewModel.currencyRate.value!!
                for (i in 0 until wallets.size) {
                    if (wallets[i].currency == CurrencyTypes.USD) {
                        sumOfDollar += wallets[i].balance
                        sumOfRuble += wallets[i].balance * curr
                    } else {
                        sumOfDollar += wallets[i].balance / curr
                        sumOfRuble += wallets[i].balance
                    }
                }
                sum_amount_rub_tv.text = String.format("\u20BD %.2f", sumOfRuble)
                sum_amount_usd_tv.text = String.format("$ %.2f", sumOfDollar)
            }
        })
    }

    private fun removeObservers() {
        viewModel.transactions.removeObservers(this)
        viewModel.wallets.removeObservers(this)
    }

    private fun setupRecyclerView(view: View) {
        transaction_rv.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)
        transactionAdapter = MainListAdapter(context)
        transaction_rv.adapter = transactionAdapter
        transaction_rv.isNestedScrollingEnabled = false
    }

    private fun setupViewPager(view: View) {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        cards_viewpager.adapter = cardAdapter
        cards_viewpager.offscreenPageLimit = 3
        cards_viewpager.clipToPadding = false
        cards_viewpager.setPadding(96, 0, 96, 0)
        cards_viewpager.pageMargin = 48
    }

}