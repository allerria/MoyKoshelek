package ru.yandex.moykoshelek.ui.balance

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_balance.*
import ru.yandex.moykoshelek.ui.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.database.entities.WalletData
import ru.yandex.moykoshelek.data.datasource.CurrencyPref
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.repositories.TransactionsRepository
import ru.yandex.moykoshelek.data.repositories.WalletRepository
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
    lateinit var walletInteractor: WalletInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchDataFromDb(view)
        fetchTransactionsFromDb(view)
    }

    private fun fetchTransactionsFromDb(view: View) {
        val task = Runnable {
            val test = walletInteractor.getTransactions()
            test.observeForever {
                (activity as MainActivity).uiHandler.post {
                    setupRecyclerView(view, it!!)
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun setupRecyclerView(view: View, data: List<TransactionData>) {
        transaction_rv.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)
        transactionAdapter = MainListAdapter(data, context)
        transaction_rv.adapter = transactionAdapter
        transaction_rv.isNestedScrollingEnabled = false
    }

    private fun setupViewPager(view: View, data: List<WalletData>) {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        for (i in 0 until data.size) {
            cardAdapter.addCardItem(data[i])
        }
        cardAdapter.notifyDataSetChanged()
        cards_viewpager.adapter = cardAdapter
        cards_viewpager.offscreenPageLimit = 3
        cards_viewpager.clipToPadding = false
        cards_viewpager.setPadding(96, 0, 96, 0)
        cards_viewpager.pageMargin = 48

        var sumOfDollar = 0.0
        var sumOfRuble = 0.0
        val test = walletInteractor.getCurrencyRate()
        test.observeForever {
            val curr = walletInteractor.getCurrencyRate().value!!
            for (i in 0 until data.size) {
                if (data[i].currency == CurrencyTypes.USD) {
                    sumOfDollar += data[i].balance
                    sumOfRuble += data[i].balance * curr
                } else {
                    sumOfDollar += data[i].balance / curr
                    sumOfRuble += data[i].balance
                }
            }
            view.findViewById<TextView>(R.id.sum_amount_rub_tv).text = String.format("\u20BD %.2f", sumOfRuble)
            view.findViewById<TextView>(R.id.sum_amount_usd_tv).text = String.format("$ %.2f", sumOfDollar)
        }
    }

    private fun fetchDataFromDb(view: View) {
        val task = Runnable {
            val test = walletInteractor.getWallets()
            test.observeForever {
                (activity as MainActivity).uiHandler.post {
                    setupViewPager(view, it!!)
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }
}