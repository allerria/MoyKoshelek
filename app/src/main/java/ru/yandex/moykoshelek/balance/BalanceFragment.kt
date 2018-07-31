package ru.yandex.moykoshelek.balance

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ru.yandex.moykoshelek.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.models.database.entities.TransactionData
import ru.yandex.moykoshelek.models.database.entities.WalletData
import ru.yandex.moykoshelek.common.CurrencyPref
import ru.yandex.moykoshelek.common.BaseFragment
import ru.yandex.moykoshelek.common.CurrencyTypes
import ru.yandex.moykoshelek.common.Screens

class BalanceFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_main
    override val TAG = Screens.BALANCE_SCREEN

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var cardAdapter: CardsPagerAdapter

    private lateinit var transactionRV: RecyclerView
    private lateinit var transactionAdapter: MainListAdapter

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
            val data = (activity as MainActivity).appDb?.transactionDataDao()?.getAll()
            (activity as MainActivity).uiHandler.post {
                if (data != null) {
                    setupRecyclerView(view, data)
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun setupRecyclerView(view: View, data: List<TransactionData>) {
        transactionRV = view.findViewById(R.id.last_history_rv)
        transactionRV.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)
        transactionAdapter = MainListAdapter(data, context)
        transactionRV.adapter = transactionAdapter
        transactionRV.isNestedScrollingEnabled = false
    }

    private fun setupViewPager(view: View, data: List<WalletData>) {
        viewPager = view.findViewById(R.id.cards_viewpager)
        tabLayout = view.findViewById(R.id.tab_dots) as TabLayout
        tabLayout.setupWithViewPager(viewPager, true)
        cardAdapter = CardsPagerAdapter()
        for (i in 0 until data.size){
            cardAdapter.addCardItem(data[i])
        }
        cardAdapter.notifyDataSetChanged()
        viewPager.adapter = cardAdapter
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.setPadding(96, 0, 96, 0)
        viewPager.pageMargin = 48

        var sumOfDollar = 0.0
        var sumOfRuble = 0.0
        val curr = CurrencyPref(this.context!!).getCurrentConvert()
        for (i in 0 until data.size){
            if (data[i].currency == CurrencyTypes.USD){
                sumOfDollar += data[i].balance
                sumOfRuble += data[i].balance * curr
            }
            else {
                sumOfDollar += data[i].balance / curr
                sumOfRuble += data[i].balance
            }
        }
        view.findViewById<TextView>(R.id.sum_amount_rub_tv).text = String.format("\u20BD %.2f", sumOfRuble)
        view.findViewById<TextView>(R.id.sum_amount_usd_tv).text = String.format("$ %.2f", sumOfDollar)
    }

    private fun fetchDataFromDb(view: View) {
        val task = Runnable {
            val data = (activity as MainActivity).appDb?.walletDataDao()?.getAll()
            (activity as MainActivity).uiHandler.post {
                if (data != null) {
                    setupViewPager(view, data)
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }
}