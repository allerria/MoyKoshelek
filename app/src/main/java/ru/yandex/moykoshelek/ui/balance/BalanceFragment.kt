package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.card_view.view.*
import kotlinx.android.synthetic.main.fragment_balance.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import timber.log.Timber
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
        initObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupViewPager()
        Timber.d("onViewCreated")
    }

    private fun initObservers() {

        viewModel.wallets.observe(this, Observer { wallets ->
            if (wallets != null && wallets.isNotEmpty()) {
                cardAdapter.setData(wallets)
                wallets.forEach { it ->
                    val view = cards_viewpager.findViewWithTag<View>(it.id)
                    if (view != null) {
                        with(view) {
                            var currency = if (CurrencyTypes.USD == it.currency) "$ " else "\u20BD "
                            currency += String.format("%.2f", it.balance)
                            card_balance.text = currency
                        }
                    }
                }
                observeTransactions(cards_viewpager.currentItem)
            }
        })

    }

    private fun setupRecyclerView() {
        transaction_rv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        transactionAdapter = MainListAdapter(context)
        transaction_rv.adapter = transactionAdapter
    }

    private fun setupViewPager() {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cards_viewpager.adapter = null
        cardAdapter = CardsPagerAdapter()
        cards_viewpager.adapter = cardAdapter
        cards_viewpager.offscreenPageLimit = 3
        cards_viewpager.clipToPadding = false
        cards_viewpager.setPadding(96, 0, 96, 0)
        cards_viewpager.pageMargin = 48
        cards_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                viewModel.getTransactions(viewModel.getWalletId(p0)).removeObservers(this@BalanceFragment)
            }

            override fun onPageSelected(p0: Int) {
                observeTransactions(p0)
            }
        })
    }

    private fun observeTransactions(walletPosition: Int) {
        viewModel.getTransactions(viewModel.getWalletId(walletPosition)).observe(this@BalanceFragment, Observer { it ->
            transactionAdapter.setData(it!!)
        })
    }

}