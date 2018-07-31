package ru.yandex.moykoshelek.transaction

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.yandex.moykoshelek.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.models.database.entities.TransactionData
import ru.yandex.moykoshelek.common.CurrencyPref
import ru.yandex.moykoshelek.common.ui.BaseFragment
import ru.yandex.moykoshelek.common.CurrencyTypes
import ru.yandex.moykoshelek.common.Screens
import ru.yandex.moykoshelek.common.TransactionTypes


class AddTransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_transaction
    override val TAG = Screens.ADD_TRANSACTION_SCREEN

    private lateinit var layout: ConstraintLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var cardAdapter: CardsPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    private val COUNTRIES = arrayOf("Belgium", "France", "Italy", "Germany", "Spain")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager(view)
        fetchUniqueCategories(view)
        layout = view.findViewById(R.id.add_transaction_layout)
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createTransaction(view) }
    }

    private fun fetchUniqueCategories(view: View) {
        val task = Runnable {
            val data = (activity as MainActivity).appDb?.transactionDataDao()?.getCategories()
            (activity as MainActivity).uiHandler.post {
                val adapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, data)
                val textView = view.findViewById(R.id.transaction_category) as AutoCompleteTextView
                textView.setAdapter<ArrayAdapter<String>>(adapter)
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
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
        transaction.typeTransaction = if(view.findViewById<RadioButton>(R.id.in_radio).isChecked) TransactionTypes.IN else TransactionTypes.OUT
        val wallet = cardAdapter.getItem(view.findViewById<ViewPager>(R.id.cards_viewpager).currentItem)
        transaction.walletId = wallet.id?.toInt()
        transaction.category = view.findViewById<AutoCompleteTextView>(R.id.transaction_category).text.toString()
        insertTransactionDataInDb(transaction)
        var balanceChange = transaction.cost
        val curr = CurrencyPref(this.context!!).getCurrentConvert()
        if (wallet.currency != transaction.currency)
            balanceChange = if(transaction.currency == CurrencyTypes.USD) transaction.cost * curr else transaction.cost / curr
        if (transaction.typeTransaction == TransactionTypes.IN)
            wallet.balance += balanceChange
        else
            wallet.balance -= balanceChange
        val task = Runnable { (activity as MainActivity).appDb?.walletDataDao()?.update(wallet) }
        (activity as MainActivity).dbWorkerThread.postTask(task)
        (activity as MainActivity).showFragment(Screens.BALANCE_SCREEN, false)
    }

    private fun insertTransactionDataInDb(data: TransactionData) {
        val task = Runnable { (activity as MainActivity).appDb?.transactionDataDao()?.insert(data) }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun setupViewPager(view: View) {
        viewPager = view.findViewById(R.id.cards_viewpager)
        tabLayout = view.findViewById(R.id.tab_dots) as TabLayout
        tabLayout.setupWithViewPager(viewPager, true)
        cardAdapter = CardsPagerAdapter()
        fetchwalletsDataFromDb()
        viewPager.adapter = cardAdapter
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.setPadding(96, 0, 96, 0)
        viewPager.pageMargin = 48
    }

    private fun fetchwalletsDataFromDb() {
        val task = Runnable {
            val data = (activity as MainActivity).appDb?.walletDataDao()?.getAll()
            (activity as MainActivity).uiHandler.post {
                if (data != null) {
                    for (i in 0 until data.size)
                        cardAdapter.addCardItem(data[i])
                    cardAdapter.notifyDataSetChanged()
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }
}