package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_period_template_transactions.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.common.BaseFragment
import javax.inject.Inject

class PeriodTemplateTransactionsFragment : BaseFragment() {
    override val TAG: String = Screens.PERIOD_TEMPLATE_SCREEN
    override val layoutRes: Int = R.layout.fragment_period_template_transactions

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: TransactionViewModel

    lateinit var templateTransactionsAdapter: TemplateTransactionsAdapter
    lateinit var periodTransactionsAdapter: PeriodTransactionsAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(TransactionViewModel::class.java)
        tab_layout.addTab(tab_layout.newTab().setText(R.string.template_transactions))
        tab_layout.addTab(tab_layout.newTab().setText(R.string.period_transactions))

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(tabLayout: TabLayout.Tab?) {
                if (tabLayout != null) {
                    if (tabLayout.position == 0) {
                        template_transaction_recycler_view.visibility = View.VISIBLE
                        period_transaction_recycler_view.visibility = View.GONE
                    } else {
                        template_transaction_recycler_view.visibility = View.GONE
                        period_transaction_recycler_view.visibility = View.VISIBLE
                    }
                }
            }
        })

        initRecyclersView()
        initObserve()
    }

    fun initObserve() = launch {
        viewModel.periodTransactions.await().observe(this@PeriodTemplateTransactionsFragment, Observer { periodTransactions ->
            if (periodTransactions != null) {
                periodTransactionsAdapter.setData(periodTransactions)
            }
        })

        viewModel.templateTransactions.await().observe(this@PeriodTemplateTransactionsFragment, Observer { templateTransactions ->
            if (templateTransactions != null) {
                templateTransactionsAdapter.setData(templateTransactions)
            }
        })
    }

    private fun initRecyclersView() {
        templateTransactionsAdapter = TemplateTransactionsAdapter { walletId, walletCurrency, transactionId -> navigateToEditAnyTransaction(ActionTypes.EDIT_TEMPLATE_TRANSACTION, walletId, walletCurrency, transactionId) }
        template_transaction_recycler_view.adapter = templateTransactionsAdapter
        template_transaction_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                val last = parent.childCount - 1
                outRect.left = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.right = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.top = resources.getDimension(R.dimen.dimen_16).toInt()
                if (position == last) {
                    outRect.bottom = resources.getDimension(R.dimen.dimen_16).toInt()
                }
            }
        })
        template_transaction_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        periodTransactionsAdapter = PeriodTransactionsAdapter { walletId, walletCurrency, transactionId -> navigateToEditAnyTransaction(ActionTypes.EDIT_TEMPLATE_TRANSACTION, walletId, walletCurrency, transactionId) }
        period_transaction_recycler_view.adapter = periodTransactionsAdapter
        period_transaction_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                val last = parent.childCount - 1
                outRect.left = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.right = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.top = resources.getDimension(R.dimen.dimen_16).toInt()
                if (position == last) {
                    outRect.bottom = resources.getDimension(R.dimen.dimen_16).toInt()
                }
            }
        })
        period_transaction_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun navigateToEditAnyTransaction(actionType: Int, walletId: Int, walletCurrency: Int, transactionId: Int) {
        router.navigateTo(Screens.TRANSACTION_SCREEN, arrayListOf(actionType, walletId, walletCurrency, transactionId))
    }

}