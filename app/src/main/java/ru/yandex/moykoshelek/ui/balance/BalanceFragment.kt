package ru.yandex.moykoshelek.ui.balance

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_balance.*
import kotlinx.android.synthetic.main.item_period_transaction.*
import kotlinx.android.synthetic.main.item_template_transaction.*
import kotlinx.android.synthetic.main.item_transaction.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.transactionTypeSign
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.transaction.ActionTypes
import timber.log.Timber
import javax.inject.Inject

class BalanceFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_balance
    override val TAG = Screens.BALANCE_SCREEN

    private lateinit var walletsAdapter: WalletsAdapter
    private var currentPosition: Int = 0
    private var currentWalletId: Int = 0
    private var currentWalletCurrency: Int = 0

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
        if (savedInstanceState != null) {
            val args = savedInstanceState.getIntegerArrayList(TAG)
            if (args != null) {
                this.currentWalletId = args[0]
                this.currentPosition = args[1]
                this.currentWalletCurrency = args[2]
            }
        }
        initWalletsRecyclerView()
        settings_button.setOnClickListener { router.navigateTo(Screens.WALLETS_SCREEN) }
        add_transaction_fab.setOnClickListener { navigateToAddTransactionOrWallet() }
        show_all_transactions.setOnClickListener { router.navigateTo(Screens.TRANSACTIONS_SCREEN, currentWalletId) }
        show_all_period_transactions.setOnClickListener { router.navigateTo(Screens.PERIOD_TEMPLATE_SCREEN) }
        Timber.d("onViewCreated")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(TAG, arrayListOf(currentWalletId, currentPosition, currentWalletCurrency))
        super.onSaveInstanceState(outState)
    }

    private fun initObservers() = launch(UI) {
        viewModel.updateCurrencyRate()
        viewModel.wallets.await().observe(this@BalanceFragment, Observer { wallets ->
            if (wallets != null && wallets.isNotEmpty()) {
                walletsAdapter.setData(wallets.plus(Wallet()))
                currentWalletId = wallets.first().id
                currentWalletCurrency = wallets.first().currency
            } else {
                walletsAdapter.setData(listOf(Wallet()))
            }
        })

        viewModel.transactions.await().observe(this@BalanceFragment, Observer { transactions ->
            setTransaction(transactions?.firstOrNull { it.walletId == currentWalletId })
        })

        viewModel.periodTransactions.await().observe(this@BalanceFragment, Observer { periodTransactions ->
            setPeriodTransaction(periodTransactions?.firstOrNull())
        })

        viewModel.templateTransactions.await().observe(this@BalanceFragment, Observer { transactions ->
            setTemplateTransaction(transactions?.firstOrNull())
        })
    }

    private fun initWalletsRecyclerView() {
        walletsAdapter = WalletsAdapter { walletId, position, walletCurrency ->
            changeWallet(walletId, position, walletCurrency)
        }
        wallet_recycler_view.adapter = walletsAdapter

        wallet_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                val last = parent.childCount - 1
                outRect.right = resources.getDimension(R.dimen.dimen_8).toInt()
                if (position % 3 == 0) {
                    outRect.left = resources.getDimension(R.dimen.dimen_16).toInt()
                }
                if (position == last) {
                    outRect.bottom = resources.getDimension(R.dimen.dimen_16).toInt()
                    outRect.right = resources.getDimension(R.dimen.dimen_16).toInt()
                }
            }
        })

        wallet_recycler_view.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(p0: View) {}

            override fun onChildViewAttachedToWindow(p0: View) {
                if (currentWalletId > 0 && currentPosition == wallet_recycler_view.getChildLayoutPosition(p0)) {
                    changeWallet(currentWalletId, currentPosition, currentWalletCurrency)
                }
            }

        })
    }

    private fun changeWallet(walletId: Int, position: Int, walletCurrency: Int) {
        if (wallet_recycler_view.childCount > position && wallet_recycler_view.childCount > currentPosition && walletId > 0) {
            wallet_recycler_view.getChildAt(currentPosition).setBackgroundResource(R.drawable.wallet_background)
            wallet_recycler_view.getChildAt(position).setBackgroundResource(R.drawable.wallet_active_background)
            currentPosition = position
            currentWalletId = walletId
            currentWalletCurrency = walletCurrency
            launch(UI) {
                setTransaction(viewModel.transactions.await().value!!.firstOrNull { it.walletId == walletId })
            }
        }
        if (walletId == 0) {
            router.navigateTo(Screens.WALLET_SCREEN)
        }
    }

    private fun setTransaction(transaction: Transaction?) {
        if (transaction != null) {
            category_text_view.text = transaction.category
            cost_text_view.text = transactionTypeSign(transaction.type) + transaction.cost.formatMoney(transaction.currency)
            date_text_view.text = transaction.date.format()
            last_transaction.setOnClickListener { navigateToEditAnyTransaction(ActionTypes.EDIT_TRANSACTION, transaction.id) }
        } else {
            category_text_view.text = "Нет данных"
            cost_text_view.text = ""
            date_text_view.text = ""
        }
    }

    private fun setPeriodTransaction(periodTransaction: PeriodTransaction?) {
        if (periodTransaction != null) {
            p_category_text_view.text = periodTransaction.category
            p_cost_text_view.text = transactionTypeSign(periodTransaction.type) + periodTransaction.cost.formatMoney(periodTransaction.currency)
            p_date_text_view.text = periodTransaction.time.format()
            p_period_text_view.text = periodTransaction.period.toString()
            last_period_transaction.setOnClickListener { navigateToEditAnyTransaction(ActionTypes.EDIT_PERIOD_TRANSACTION, periodTransaction.id) }
        } else {
            p_category_text_view.text = "Нет данных"
            p_cost_text_view.text = ""
            p_date_text_view.text = ""
            p_period_text_view.text = ""
        }
    }

    fun setTemplateTransaction(templateTransaction: TemplateTransaction?) {
        if (templateTransaction != null) {
            t_category_text_view.text = templateTransaction.category
            t_name_text_view.text = templateTransaction.name
            t_cost_text_view.text = transactionTypeSign(templateTransaction.type) + templateTransaction.cost.formatMoney(templateTransaction.currency)
            t_date_text_view.text = templateTransaction.time.format()
            t_period_text_view.text = templateTransaction.period.toString()
            last_template_transaction.setOnClickListener { navigateToEditAnyTransaction(ActionTypes.EDIT_TEMPLATE_TRANSACTION, templateTransaction.id) }
        } else {
            t_category_text_view.text = "Нет данных"
            t_name_text_view.text = ""
            t_cost_text_view.text = ""
            t_date_text_view.text = ""
            t_period_text_view.text = ""
        }
    }

    private fun navigateToEditAnyTransaction(actionType: Int, transactionId: Int) {
        router.navigateTo(Screens.TRANSACTION_SCREEN, arrayListOf(actionType, currentWalletId, currentWalletCurrency, transactionId))
    }

    private fun navigateToAddTransactionOrWallet() = launch(UI) {
        if (viewModel.wallets.await().value!!.isNotEmpty()) {
            router.navigateTo(Screens.TRANSACTION_SCREEN, arrayListOf(ActionTypes.ADD_TRANSACTION, currentWalletId, currentWalletCurrency))
        } else {
            router.navigateTo(Screens.WALLET_SCREEN)
        }
    }

}