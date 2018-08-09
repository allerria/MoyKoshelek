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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.extensions.currencySign
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

    @Inject
    lateinit var router: Router

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(BalanceViewModel::class.java)
        initObservers()
        viewModel.checkPeriodTransactions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupViewPager()
        Timber.d("onViewCreated")
    }

    private fun initObservers() = launch(UI) {
        viewModel.updateCurrencyRate()
        viewModel.wallets.await().observe(this@BalanceFragment, Observer { wallets ->
            if (wallets != null && wallets.isNotEmpty()) {
                cardAdapter.setData(wallets)
                wallets.forEach { it ->
                    val view = cards_viewpager.findViewWithTag<View>(it.id)
                    if (view != null) {
                        with(view) {
                            var currency = currencySign(it.currency)
                            currency += String.format("%.2f", it.balance)
                            card_balance.text = currency
                        }
                    }
                }
                changeWallet(cardAdapter.getItem(cards_viewpager.currentItem).id)
            }
        })

        viewModel.transactions.await().observe(this@BalanceFragment, Observer { transactions ->
            if (transactions != null) {
                transactionAdapter.setTransactions(transactions)
                if (cardAdapter.count > 0) {
                    changeWallet(cardAdapter.getItem(cards_viewpager.currentItem).id)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        transaction_rv.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        transactionAdapter = MainListAdapter({ navigateToEditTransaction(it) })
        transaction_rv.adapter = transactionAdapter
    }

    private fun setupViewPager() {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        with(cards_viewpager) {
            adapter = cardAdapter
            clipToPadding = false
            setPadding(resources.getDimension(R.dimen.dimen_48).toInt(), 0, resources.getDimension(R.dimen.dimen_48).toInt(), 0)
            pageMargin = resources.getDimension(R.dimen.dimen_48).toInt()
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(p0: Int) {
                    changeWallet(cardAdapter.getItem(p0).id)
                }
            })
        }
    }

    private fun changeWallet(walletId: Int) {
        transactionAdapter.setData(walletId)
    }

    private fun navigateToEditTransaction(transactionId: Int) {
        router.navigateTo(Screens.TRANSACTION_SCREEN, transactionId)
    }

}