package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class TransactionsFragment : BaseFragment() {
    override val TAG: String = Screens.TRANSACTIONS_SCREEN
    override val layoutRes: Int = R.layout.fragment_transactions

    companion object {
        fun getInstance(walletId: Int) = TransactionsFragment().apply {
            arguments = Bundle().apply {
                putInt(TAG, walletId)
            }
        }
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: TransactionViewModel

    lateinit var transactionsAdapter: TransactionsAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var walletId = 0
    private var walletPosition = 0
    private var walletCurrency = -1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(TransactionViewModel::class.java)
        walletId = arguments!!.getInt(TAG)
        add_transaction_fab.setOnClickListener { router.navigateTo(Screens.TRANSACTION_SCREEN, arrayListOf(ActionTypes.ADD_TRANSACTION, walletId, walletCurrency)) }
        initRecyclerView()
        initObserve()
    }

    fun initObserve() = launch {
        viewModel.wallets.await().observe(this@TransactionsFragment, Observer { wallets ->
            if (wallets != null) {
                initWalletSpinner(wallets)
            }
        })

        viewModel.transactions.await().observe(this@TransactionsFragment, Observer { transactions ->
            if (transactions != null) {
                transactionsAdapter.setTransactions(transactions)
                transactionsAdapter.setData(walletId)
            }
        })
    }

    private fun initRecyclerView() {
        transactionsAdapter = TransactionsAdapter { navigateToEditAnyTransaction(ActionTypes.EDIT_TRANSACTION, it) }
        transaction_recycler_view.adapter = transactionsAdapter
        transaction_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
        transaction_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun navigateToEditAnyTransaction(actionType: Int, transactionId: Int) {
        router.navigateTo(Screens.TRANSACTION_SCREEN, arrayListOf(actionType, walletId, walletCurrency, transactionId))
    }

    private fun initWalletSpinner(wallets: List<Wallet>) {
        val walletStringArray = mutableListOf<String>()

        walletStringArray.addAll(listOf(getString(R.string.choose_wallet)).plus(wallets.map { "${it.name}-${it.balance.formatMoney(it.currency)}" }))

        wallet_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, walletStringArray)
        val it = wallets.find { it.id == walletId }!!
        walletCurrency = it.currency
        walletPosition = walletStringArray.indexOf("${it.name}-${it.balance.formatMoney(it.currency)}")
        launch(UI) {
            delay(166)
            wallet_spinner.setSelection(walletPosition)
        }

        wallet_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>, itemView: View, position: Int, p3: Long) {
                if (position > 0) {
                    launch(UI) {
                        walletId = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().id
                        walletCurrency = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().currency
                        transactionsAdapter.setData(walletId)
                    }
                }
            }

        }
    }

}